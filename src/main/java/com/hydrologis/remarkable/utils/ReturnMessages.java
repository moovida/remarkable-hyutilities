/*******************************************************************************
 * Copyright (C) 2018 HydroloGIS S.r.l. (www.hydrologis.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Antonello Andrea (http://www.hydrologis.com)
 ******************************************************************************/
package com.hydrologis.remarkable.utils;
/**
 * Http return messages.
 * 
 * @author Antonello Andrea (www.hydrologis.com)
 */
public enum ReturnMessages {
    HTTP_OK("OK", null), // //$NON-NLS-1$
    HTTP_FORBIDDEN("Forbidden", null), // //$NON-NLS-1$
    HTTP_UNAUTHORIZED("Unauthorized", null), // //$NON-NLS-1$
    HTTP_NOT_FOUND("Not found", null), // //$NON-NLS-1$
    UNKNOWN("Unknown", null); //$NON-NLS-1$

    private String message;
    private byte[] optionalBytes;

    private ReturnMessages( String message, byte[] optionalBytes ) {
        this.message = message;
        this.optionalBytes = optionalBytes;
    }

    public String getMessage() {
        return message;
    }

    public void setOptionalBytes( byte[] optionalBytes ) {
        this.optionalBytes = optionalBytes;
    }

    public byte[] getOptionalBytes() {
        return optionalBytes;
    }

}
