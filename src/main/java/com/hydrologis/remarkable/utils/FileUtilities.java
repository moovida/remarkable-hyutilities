/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.hydrologis.remarkable.utils;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p> Various file utilities useful when dealing with bytes, bits and numbers </p>
 *
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class FileUtilities {

    public static void copyFile( String fromFile, String toFile ) throws IOException {
        Files.copy(Paths.get(fromFile), Paths.get(toFile));
    }

    public static void copyFile( File in, File out ) throws IOException {
        copyFile(in.getAbsolutePath(), out.getAbsolutePath());
    }

    public static long getCreationTimestamp( String path ) throws IOException {
        Path pathObj = Paths.get(path);
        BasicFileAttributes attr = Files.readAttributes(pathObj, BasicFileAttributes.class);

        return attr.creationTime().toMillis();
    }

    /**
     * Returns true if all deletions were successful. If a deletion fails, the method stops attempting to delete and
     * returns false.
     *
     * @param filehandle the file or folder to remove.
     *
     * @return true if all deletions were successful
     * @throws IOException 
     */
    public static boolean deleteFileOrDir( File filehandle ) throws IOException {

        if (filehandle.isDirectory()) {
            Files.walkFileTree(Paths.get(filehandle.getAbsolutePath()), new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory( Path dir, IOException exc ) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        boolean isdel = filehandle.delete();
        if (!isdel) {
            // if it didn't work, which somtimes happens on windows systems,
            // remove on exit
            filehandle.deleteOnExit();
        }

        return isdel;
    }

    /**
     * Read a file into a byte array.
     *
     * @param filePath the path to the file.
     *
     * @return the array of bytes.
     *
     * @throws IOException
     */
    public static byte[] readFileToBytes( String filePath ) throws IOException {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        return bytes;
    }

    /**
     * Read from an inoutstream and convert the readed stuff to a String. Usefull for text files that are available as
     * streams.
     *
     * @param inputStream
     *
     * @return the read string
     *
     * @throws IOException
     */
    public static String readInputStreamToString( InputStream inputStream ) throws IOException {
        // Create the byte list to hold the data
        List<Byte> bytesList = new ArrayList<Byte>();

        byte b = 0;
        while( (b = (byte) inputStream.read()) != -1 ) {
            bytesList.add(b);
        }
        // Close the input stream and return bytes
        inputStream.close();

        byte[] bArray = new byte[bytesList.size()];
        for( int i = 0; i < bArray.length; i++ ) {
            bArray[i] = bytesList.get(i);
        }

        String file = new String(bArray);
        return file;
    }

    /**
     * Read text from a file in one line.
     *
     * @param filePath the path to the file to read.
     *
     * @return the read string.
     *
     * @throws IOException
     */
    public static String readFile( String filePath ) throws IOException {
        return readFile(new File(filePath));
    }

    /**
     * Read text from a file in one line.
     *
     * @param file the file to read.
     *
     * @return the read string.
     *
     * @throws IOException
     */
    public static String readFile( File file ) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("The required projection file is not available: " + file.getAbsolutePath());
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF-8"))) {
            StringBuilder sb = new StringBuilder(200);
            String line;
            while( (line = br.readLine()) != null ) {
                sb.append(line);
                sb.append("\n"); //$NON-NLS-1$
            }
            return sb.toString();
        }
    }

    /**
     * Read text from a file to a list of lines.
     *
     * @param filePath the path to the file to read.
     *
     * @return the list of lines.
     *
     * @throws IOException
     */
    public static List<String> readFileToLinesList( String filePath ) throws IOException {
        return readFileToLinesList(new File(filePath));
    }

    /**
     * Read text from a file to a list of lines.
     *
     * @param file the file to read.
     *
     * @return the list of lines.
     *
     * @throws IOException
     */
    public static List<String> readFileToLinesList( File file ) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while( (line = br.readLine()) != null ) {
                lines.add(line);
            }
            return lines;
        }
    }

    /**
     * Write text to a file in one line.
     *
     * @param text the text to write.
     * @param file the file to write to.
     *
     * @throws IOException
     */
    public static void writeFile( String text, File file ) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            bw.write(text);
        }
    }

    /**
     * Write a list of lines to a file.
     *
     * @param lines    the list of lines to write.
     * @param filePath the path to the file to write to.
     *
     * @throws IOException
     */
    public static void writeFile( List<String> lines, String filePath ) throws IOException {
        writeFile(lines, new File(filePath));
    }

    /**
     * Write a list of lines to a file.
     *
     * @param lines the list of lines to write.
     * @param file  the file to write to.
     *
     * @throws IOException
     */
    public static void writeFile( List<String> lines, File file ) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for( String line : lines ) {
                bw.write(line);
                bw.write("\n"); //$NON-NLS-1$
            }
        }
    }

    public static String replaceBackSlashes( String path ) {
        return path.replaceAll("\\\\", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Replaces backslashes with /.
     * 
     * @param string the string to check.
     * @return the string without backslashes.
     */
    public static String replaceBackSlashesWithSlashes( String string ) {
        if (string != null) {
            string = string.replaceAll("\\\\", "/").replaceAll("\\\\\\\\", "/");
        }
        return string;
    }

    /**
     * Returns the name of the file without the extension.
     * <p/>
     * <p>Note that if the file has no extension, the name is returned.
     *
     * @param file the file to trim.
     *
     * @return the name without extension.
     */
    public static String getNameWithoutExtention( File file ) {
        String name = file.getName();
        int lastDot = name.lastIndexOf("."); //$NON-NLS-1$
        if (lastDot == -1) {
            // file has no extension, return the name
            return name;
        }
        name = name.substring(0, lastDot);
        return name;
    }

    /**
     * Substitute the extention of a file.
     *
     * @param file         the file.
     * @param newExtention the new extention (without the dot).
     *
     * @return the file with the new extention.
     */
    public static File substituteExtention( File file, String newExtention ) {
        String path = file.getAbsolutePath();
        int lastDot = path.lastIndexOf("."); //$NON-NLS-1$
        if (lastDot == -1) {
            path = path + "." + newExtention; //$NON-NLS-1$
        } else {
            path = path.substring(0, lastDot) + "." + newExtention; //$NON-NLS-1$
        }
        return new File(path);
    }

    /**
     * Makes a file name safe to be used.
     *
     * @param fileName the file name to "encode".
     * @return the safe filename.
     */
    public static String getSafeFileName( String fileName ) {
        // not allowed chars in win and linux
        char[] notAllowed = new char[]{'/', '>', '<', ':', '"', '/', '\\', '|', '?', '*'};
        char escape = '_';
        int len = fileName.length();
        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ ) {
            char ch = fileName.charAt(i);

            boolean trapped = false;
            for( char forbidden : notAllowed ) {
                if (ch == forbidden) {
                    sb.append(escape);
                    trapped = true;
                    break;
                }
            }

            if (!trapped) {
                sb.append(ch);
            }
        }
        String newName = sb.toString();
        // no space at end is allowed
        newName = newName.trim();

        // win doesn't allow dots at the end
        if (newName.endsWith(".")) {
            newName = newName.substring(0, newName.length() - 1);
        }

        // reserved words for windows
        String[] reservedWindows = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8",
                "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

        for( String reservedWin : reservedWindows ) {
            if (newName.equals(reservedWin)) {
                newName = newName + "_";
                break;
            }
        }

        return newName;
    }

    /**
     * Method to read a properties file into a {@link LinkedHashMap}. <p/> <p>Empty lines are ignored, as well as lines
     * that do not contain the separator.</p>
     *
     * @param filePath   the path to the file to read.
     * @param separator  the separator or <code>null</code>. Defaults to '='.
     * @param valueFirst if <code>true</code>, the second part of the string is used as key.
     *
     * @return the read map.
     *
     * @throws IOException
     */
    public static LinkedHashMap<String, String> readFileToHashMap( String filePath, String separator, boolean valueFirst )
            throws IOException {
        if (separator == null) {
            separator = "=";
        }
        List<String> lines = readFileToLinesList(filePath);
        LinkedHashMap<String, String> propertiesMap = new LinkedHashMap<>();
        for( String line : lines ) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (!line.contains(separator)) {
                continue;
            }
            String[] lineSplit = line.split(separator);
            if (!valueFirst) {
                String key = lineSplit[0].trim();
                String value = "";
                if (lineSplit.length > 1) {
                    value = lineSplit[1].trim();
                }
                propertiesMap.put(key, value);
            } else {
                if (lineSplit.length > 1) {
                    String key = lineSplit[0].trim();
                    String value = lineSplit[1].trim();
                    propertiesMap.put(value, key);
                }
            }
        }
        return propertiesMap;
    }

    /**
     * "Converts" a string to a temporary file. <p/> <p>Useful for those modules that want a file in input and one wants
     * to use the parameters.</p>
     *
     * @param string the string to write to the file.
     *
     * @return the created file.
     *
     * @throws Exception
     */
    public static File stringAsTmpFile( String string ) throws Exception {
        File tempFile = File.createTempFile("jgt-", "txt");
        writeFile(string, tempFile);
        return tempFile;
    }

    /**
     * "Converts" a List of strings to a temporary file. <p/> <p>Useful for those modules that want a file in input and
     * one wants to use the parameters.</p>
     *
     * @param list the list of strings to write to the file (one per row).
     *
     * @return the created file.
     *
     * @throws Exception
     */
    public static File stringListAsTmpFile( List<String> list ) throws Exception {
        File tempFile = File.createTempFile("jgt-", "txt");
        writeFile(list, tempFile);
        return tempFile;
    }

    /**
     * Get the list of files in a folder by its extension.
     *
     * @param folderPath the folder path.
     * @param ext        the extension without the dot.
     *
     * @return the list of files patching.
     */
    public static File[] getFilesListByExtention( String folderPath, final String ext ) {
        File[] files = new File(folderPath).listFiles(new FilenameFilter(){
            public boolean accept( File dir, String name ) {
                return name.endsWith(ext);
            }
        });
        return files;
    }

}
