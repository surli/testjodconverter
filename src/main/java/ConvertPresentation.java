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
import java.io.FileInputStream;
import java.io.InputStream;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.JsonDocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class ConvertPresentation
{
    public static void main(String[] args) throws Exception
    {
        File inputFile = new File("/tmp/jodtest/input/export_input.html");
        File outputFile = new File("/tmp/jodtest/output/export_output.odt");
        InputStream docFormatInput = new FileInputStream(new File("/tmp/jodtest/document-formats.js"));

        DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
        OfficeManager officeManager = configuration.setOfficeHome(new File(args[0])).buildOfficeManager();

        System.out.println("Starting office server...");
        officeManager.start();
        System.out.println("Server started.");

        try {
            OfficeDocumentConverter officeDocumentConverter = new OfficeDocumentConverter(officeManager
                , new JsonDocumentFormatRegistry(docFormatInput));

            System.out.println("Starting conversion....");
            officeDocumentConverter.convert(inputFile, outputFile);
            System.out.println("Conversion finished.");
        } finally {
            System.out.println("Stopping office server...");
            officeManager.stop();
            System.out.println("Server stopped.");
        }
    }
}
