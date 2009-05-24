package org.codehaus.mojo.unix.dpkg;

/*
 * The MIT License
 *
 * Copyright 2009 The Codehaus.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.mojo.unix.*;
import org.codehaus.mojo.unix.util.*;
import org.codehaus.mojo.unix.util.line.*;
import org.codehaus.plexus.util.*;

import java.io.*;

/**
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ControlFile
    implements LineProducer
{
    private static final String EOL = System.getProperty( "line.separator" );

    public String groupId;

    public String artifactId;

    public PackageVersion version;

//    public Set<DebianDependency> dependencies;

    // Generic

    public String description;

    // TODO: This should be renamed to name
    public String shortDescription;

    public String maintainer;

    // Debian specific
    // TODO: This should be renamed to "id" or something similar in the dpkg nomenclature
    public String _package;

    public String architecture = "any";

    public String priority;

    public String section;

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

//    public String getDepends()
//    {
//        if ( dependencies == null || dependencies.size() <= 0 )
//        {
//            return null;
//        }
//
//        String depends = "";
//
//        for ( DebianDependency debianDependency : dependencies )
//        {
//            if ( depends.length() > 0 )
//            {
//                depends += ", ";
//            }
//
//            // This will happen if this is an extra dependency
//            if ( StringUtils.isNotEmpty( debianDependency.getGroupId() ) )
//            {
//                depends += debianDependency.getGroupId() + "-";
//            }
//
//            depends += debianDependency.getArtifactId() + " ";
//
//            // This will happen if this is an extra dependency
//            if ( StringUtils.isNotEmpty( debianDependency.getVersion() ) )
//            {
//                depends += "(" + debianDependency.getVersion() + ")";
//            }
//        }
//
//        return depends;
//    }

    public String getPackage()
    {
        if ( _package != null )
        {
            return _package;
        }

        if ( StringUtils.isEmpty( groupId ) || StringUtils.isEmpty( artifactId ) )
        {
            throw new RuntimeException( "Both group id and artifact id has to be set." );
        }

        String name = groupId + "-" + artifactId;

        name = name.toLowerCase();

        return name;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public void streamTo( LineStreamWriter control )
    {
        control.
            add( "Section: " + UnixUtil.getField( "section", section ) ).
            add( "Priority: " + UnixUtil.getFieldOrDefault( priority, "standard" ) ).
            add( "Maintainer: " + UnixUtil.getField( "maintainer", maintainer ) ).
            add( "Package: " + getPackage() ).
            add( "Version: " + getDebianVersion( version ) ).
            add( "Architecture: " + UnixUtil.getField( "architecture", architecture ) ).
//        String depends = getDepends();
//        if ( depends != null )
//        {
//            output.println( "Depends: " + depends );
//        }
            add( "Description: " + getDebianDescription() );
    }

    public static String getDebianVersion( PackageVersion version )
    {
        String v = version.version;

        if ( version.revision.isSome() )
        {
            // It is assumed that this is validated elsewhere (in the dpkg mojo helper to be specific)
            v += "-" + Integer.parseInt( version.revision.some() );
        }

        if ( !version.snapshot )
        {
            return v;
        }

        return v + "-" + version.timestamp;
    }

    public String getDebianDescription()
    {
        // ----------------------------------------------------------------------
        // If the short description is set, use it. If not, synthesize one.
        // ----------------------------------------------------------------------

        String sd = StringUtils.clean( shortDescription );

        String d = StringUtils.clean( description );

        if ( sd.length() == 0 )
        {
            int index = d.indexOf( '.' );

            if ( index > 0 )
            {
                sd = d.substring( 0, index + 1 );

                d = d.substring( index + 1 );
            }
        }

        sd = sd.trim();
        d = d.trim();

        if ( d.length() > 0 )
        {
            d = sd + EOL + d;
        }
        else
        {
            d = sd;
        }

        // ----------------------------------------------------------------------
        // Trim each line, replace blank lines with " ."
        // ----------------------------------------------------------------------

        String debianDescription;

        try
        {
            BufferedReader reader = new BufferedReader( new StringReader( d.trim() ) );

            String line;

            debianDescription = reader.readLine();

            line = reader.readLine();

            if ( line != null )
            {
                debianDescription += EOL + " " + line.trim();

                line = reader.readLine();
            }

            while ( line != null )
            {
                line = line.trim();

                if ( line.equals( "" ) )
                {
                    debianDescription += EOL + ".";
                }
                else
                {
                    debianDescription += EOL + " " + line;
                }

                line = reader.readLine();
            }
        }
        catch ( IOException e )
        {
            // This won't happen.
            throw new RuntimeException( "Internal error", e );
        }

        return debianDescription;
    }
}