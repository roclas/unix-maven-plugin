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
import org.apache.maven.plugin.*;
import com.roclas.mojo.unix.*;
import com.roclas.mojo.unix.maven.*;
import static com.roclas.mojo.unix.maven.plugin.MavenProjectWrapper.*;
import static com.roclas.mojo.unix.util.ScriptUtil.Strategy.*;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public abstract class AbstractPackageMojo<UP extends UnixPackage<UP, PP>, PP extends UnixPackage.PreparedPackage>
    extends AbstractUnixMojo
{
    /**
     * The set of packages to build.
     *
     * @parameter
     */
    @SuppressWarnings( {"UnusedDeclaration"} )
    private Package[] packages;

    private final String platformType;

    private final String formatType;

    private final String artifactType;

    protected AbstractPackageMojo( String platformType, String formatType, String artifactType )
    {
        this.platformType = platformType;

        this.formatType = formatType;

        this.artifactType = artifactType;
    }

    protected abstract F<UP, UP> getValidateMojoSettingsAndApplyFormatSpecificSettingsToPackageF();

    public final void execute()
        throws MojoExecutionException, MojoFailureException
    {
        MojoHelper.create( platforms,
                           platformType,
                           formatType,
                           mavenProjectWrapper( project, session.get() ),
                           debug,
                           false,
                           getValidateMojoSettingsAndApplyFormatSpecificSettingsToPackageF(),
                           new PackagingMojoParameters( name,
                                                        revision,
                                                        description,
                                                        contact,
                                                        contactEmail,
                                                        architecture,
                                                        defaults,
                                                        assembly,
                                                        packages ),
                           getLog() ).
            execute( artifactType, project, mavenProjectHelper, SINGLE );
    }
}
