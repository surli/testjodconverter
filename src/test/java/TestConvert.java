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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.filter.text.LinkedImagesEmbedderFilter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestConvert
{
    @Test
    public void testConvert() throws OfficeException, IOException
    {
        File inputFile = new File("src/test/resources/ppt/Test.ppt");
        File outputDir = new File("/tmp/jodtest/");
        File outputFile = new File(outputDir, "img0.html");

        LocalOfficeManager.Builder config = LocalOfficeManager.builder();
        LocalOfficeManager officeManager = config.build();
        officeManager.start();
        LocalConverter localConverter = LocalConverter.builder()
            .officeManager(officeManager)
            .filterChain(new LinkedImagesEmbedderFilter())
            .build();

        localConverter.convert(inputFile)
            .to(outputFile)
            .execute();
        officeManager.stop();

        Stream<Path> pathList = Files.list(Path.of("/tmp/jodtest/"));
        Optional<Path> first = pathList.filter(path -> path.toString().contains(".jpg")).findFirst();
        assertFalse(first.isEmpty());
    }
}
