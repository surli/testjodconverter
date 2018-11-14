import org.jodconverter.filter.Filter;
import org.jodconverter.filter.FilterChain;
import org.jodconverter.office.LocalOfficeContext;
import org.jodconverter.office.OfficeContext;

import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XIndexAccess;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.text.XTextGraphicObjectsSupplier;
import com.sun.star.uno.UnoRuntime;

/** This filter is used to embed external images into a document. */
public class ImageEmbedderFilter implements Filter {

  @Override
  public void doFilter(
      final OfficeContext context, final XComponent document, final FilterChain chain)
      throws Exception {

    if (UnoRuntime.queryInterface(XServiceInfo.class, document)
        .supportsService("com.sun.star.text.GenericTextDocument")) {
      embedWriterImages(document, context);
    }

    // Invoke the next filter in the chain
    chain.doFilter(context, document);
  }

  private void embedWriterImages(final XComponent document, final OfficeContext context)
      throws Exception {

    final LocalOfficeContext localContext = (LocalOfficeContext) context;

    final XIndexAccess indexAccess =
        UnoRuntime.queryInterface(
            XIndexAccess.class,
            UnoRuntime.queryInterface(XTextGraphicObjectsSupplier.class, document)
                .getGraphicObjects());
    final XGraphicProvider graphicProvider =
        UnoRuntime.queryInterface(
            XGraphicProvider.class,
            localContext
                .getComponentContext()
                .getServiceManager()
                .createInstanceWithContext(
                    "com.sun.star.graphic.GraphicProvider", localContext.getComponentContext()));
    final PropertyValue[] queryProperties = new PropertyValue[] {new PropertyValue()};
    queryProperties[0].Name = "URL";
    for (int i = 0; i < indexAccess.getCount(); i++) {
      try {
        final XPropertySet graphicProperties =
            UnoRuntime.queryInterface(XPropertySet.class, indexAccess.getByIndex(i));
        final String graphicURL = (String) graphicProperties.getPropertyValue("GraphicURL");
        if (!graphicURL.contains("vnd.sun.star.GraphicObject")) {
          queryProperties[0].Value = graphicURL;
          // Before embedding the image, the "ActualSize" property holds the image
          // size specified in the document content. If the width or height are not
          // specified then their actual values will be 0.
          final Size specifiedSize =
              UnoRuntime.queryInterface(
                  Size.class, graphicProperties.getPropertyValue("ActualSize"));
          graphicProperties.setPropertyValue(
              "Graphic", graphicProvider.queryGraphic(queryProperties));
          // Images are embedded as characters (see TextContentAnchorType.AS_CHARACTER)
          // and their size is messed up if it's not explicitly specified (e.g. if the
          // image height is not specified then it takes the line height).
          adjustImageSize(graphicProperties, specifiedSize);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void adjustImageSize(final XPropertySet graphicProperties, final Size specifiedSize) {

    try {
      // After embedding the image, the "ActualSize" property holds the actual image size.
      final Size size =
          UnoRuntime.queryInterface(Size.class, graphicProperties.getPropertyValue("ActualSize"));
      // Compute the width and height if not specified, preserving aspect ratio.
      if (specifiedSize.Width == 0 && specifiedSize.Height == 0) {
        specifiedSize.Width = size.Width;
        specifiedSize.Height = size.Height;
      } else if (specifiedSize.Width == 0) {
        specifiedSize.Width = specifiedSize.Height * size.Width / size.Height;
      } else if (specifiedSize.Height == 0) {
        specifiedSize.Height = specifiedSize.Width * size.Height / size.Width;
      }
      graphicProperties.setPropertyValue("Size", specifiedSize);
    } catch (Exception e) {
      // Ignore this image.
    }
  }
}