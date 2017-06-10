#!/usr/bin/perl

use strict;
use warnings;
use File::Find;

our $DOMAIN = 'enderio:';
our $dryrun = 0;

unless (@ARGV) {
    print "
    Script to rename resource files and edit json files for Minecraft mods.
    
    (1) Edit the script and enter your modid (as used in you json files). Currently: $DOMAIN
    (2) Run the script with the resource folder(s) you want to work it on as command line parameters
    (3) Check the output and the .chg files
    (4) Remove the chg files
    (5) Edit the script and set dryrun to 0
    (6) Run again
    (7) done
";  
    
    exit 0;
}

our $mod = {};

find(\&wanted, @ARGV);
find(\&wanted2, @ARGV);

sub wanted {
    return if -d $_ or $_ !~ /\.(png|json|mcmeta)$/i;
    my $fn0 = $_;
    next unless $fn0 =~ /^(.*)\.(.*?)$/;
    my ($base0, $base1, $ext0, $ext1) = ($1, $1, $2, $2);
    $ext1 =~ s/(\.[^.]+)$/lc $1/e;
    $base1 =~ s/([A-Z])/'_' . lc($1)/eg;
    if ($base0 ne $base1) {
        print "rename $File::Find::dir/$base0.$ext0 $base1.$ext1\n";
        unless ($dryrun) {
            my $temp = localtime() . $$;
#            rename "$File::Find::dir/$base0.$ext0", $temp;
            rename "$base0.$ext0", $temp;
            rename $temp, "$base1.$ext1";
        }
        if ($File::Find::dir =~ m!textures/(.*)!) {
            my $path0 = $DOMAIN . $1 . '/' . $base0;
            my $path1 = $DOMAIN . $1 . '/' . $base1;
            if (exists $mod->{$path0} and $mod->{$path0} ne $path1) {
                die "$path0 to $mod->{$path0} or $path1 ?";
            }
            $mod->{$path0} = $path1;
        }
        if ($File::Find::dir =~ m!models/(.*)!) {
            my $path0 = $DOMAIN . $1 . '/' . $base0;
            my $path1 = $DOMAIN . $1 . '/' . $base1;
            if (exists $mod->{$path0} and $mod->{$path0} ne $path1) {
                die "$path0 to $mod->{$path0} or $path1 ?";
            }
            $mod->{$path0} = $path1;
            $path0 = $DOMAIN . $base0;
            $path1 = $DOMAIN . $base1;
            if (exists $mod->{$path0} and $mod->{$path0} ne $path1) {
                die "$path0 to $mod->{$path0} or $path1 ?";
            }
            $mod->{$path0} = $path1;
        }
    }
}

sub wanted2 {
    return if -d $_ or $_ !~ /\.(json)$/i;
    my $fn = $_;
    open F, '<', $fn;
    local $/ = undef;
    my $text = <F>;
    close F;
    my $changed = undef;
    foreach my $key (keys %$mod) {
        my $pat = quotemeta $key;
        if ($text =~ s/$pat/$mod->{$key}/g) {
            $changed = 1;
        }
    }
    if ($changed) {
        print "edit $File::Find::dir/$fn\n";
        open F, '>', $fn . ($dryrun ? '.chg' : '');
        print F $text;
        close F;
    }
}
