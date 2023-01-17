/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.File;
import java.io.InputStream;

import org.jodconverter.core.document.JsonDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.filter.text.LinkedImagesEmbedderFilter;
import org.jodconverter.local.office.LocalOfficeManager;

public class TestConvert
{
    public static void main(String[] args) throws OfficeException
    {
        File inputFile = new File("src/main/resources/odp/Test.odp");
        File outputDir = new File("/tmp/jodtest/");
        File outputFile = new File(outputDir, "img0.html");

        LocalOfficeManager.Builder config = LocalOfficeManager.builder();
        LocalOfficeManager officeManager = config.build();
        officeManager.start();
        LocalConverter localConverter = null;
        try (InputStream input = TestConvert.class.getResourceAsStream("/document-formats.js")) {
            if (input != null) {
                localConverter = LocalConverter.builder().officeManager(officeManager)
                    .formatRegistry(JsonDocumentFormatRegistry.create(input))
                    .filterChain(new LinkedImagesEmbedderFilter()).build();
            } else {
                localConverter = LocalConverter.builder()
                    .officeManager(officeManager)
                    .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        localConverter.convert(inputFile)
            .to(outputFile)
            .execute();
        officeManager.stop();
    }
}
