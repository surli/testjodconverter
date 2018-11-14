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
import java.io.IOException;
import java.io.InputStream;

import org.jodconverter.LocalConverter;
import org.jodconverter.document.JsonDocumentFormatRegistry;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;

public class TestConvert
{
    public static void main(String[] args) throws OfficeException, IOException
    {
        File inputFile = new File("src/main/resources/html/export_input.html");
        File outputFile = new File("/tmp/jodtest/test.odt");
        InputStream docFormatInput = TestConvert.class.getResourceAsStream("/document-formats.js");

        LocalOfficeManager.Builder configuration = LocalOfficeManager.builder();
        LocalOfficeManager officeManager = configuration.build();
        officeManager.start();
        LocalConverter localConverter = LocalConverter.builder()
            .officeManager(officeManager)
            .formatRegistry(JsonDocumentFormatRegistry.create(docFormatInput))
            .filterChain(new ImageEmbedderFilter())
            .build();
        localConverter.convert(inputFile)
            .to(outputFile)
            .execute();
        officeManager.stop();
    }
}
