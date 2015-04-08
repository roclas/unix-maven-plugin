package com.roclas.mojo.unix.maven.plugin;

import com.roclas.mojo.unix.*;

public class Regex
{
    public String pattern;
    public String replacement;

    public UnixFsObject.Replacer toReplacer()
    {
        return new UnixFsObject.Replacer( pattern, replacement);
    }
}
