package com.roclas.mojo.unix.maven.plugin;

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

import fj.*;
import static com.roclas.mojo.unix.maven.rpm.RpmMojoUtil.*;
import com.roclas.mojo.unix.maven.rpm.*;

/**
 * Creates an RPM file, attached to the build.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @goal package-rpm-attached
 * @phase package
 * @requiresDependencyResolution runtime
 */
@SuppressWarnings("UnusedDeclaration")
public class PackageRpmAttachedMojo
    extends AbstractPackageAttachedMojo<RpmUnixPackage, RpmUnixPackage.RpmPreparedPackage>
{
    /**
     * RPM format specific settings.
     *
     * @parameter
     */
    protected RpmSpecificSettings rpm = new RpmSpecificSettings();

    public PackageRpmAttachedMojo()
    {
        super( "linux", "rpm", "rpm" );
    }

    protected F<RpmUnixPackage, RpmUnixPackage> getValidateMojoSettingsAndApplyFormatSpecificSettingsToPackageF()
    {
        return new F<RpmUnixPackage, RpmUnixPackage>()
        {
            public RpmUnixPackage f( RpmUnixPackage unixPackage )
            {
                return validateMojoSettingsAndApplyFormatSpecificSettingsToPackage( rpm, unixPackage );
            }
        };
    }
}
