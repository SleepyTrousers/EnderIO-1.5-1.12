#!/usr/bin/perl

our $x = {};
our $y = {};
our $z = {};
our $e = {};
our $l = {};

open F, '<', '/f/github/manual/EnderIO_111/run/logs/fml-client-latest.log';

# Caused by: java.io.FileNotFoundException: enderio:models/item/block_decoration1.json

# Caused by: net.minecraft.client.renderer.block.model.ModelBlockDefinition$MissingVariantException

# [06:31:49] [Client thread/ERROR] [TEXTURE ERRORS]:       textures/items/dark_steel_shears.png

while (<F>) {
	if (/Exception loading model for variant (.*?)#(.*?) /) {
		$x->{$1}{$2}++;
    $y->{$1 . '#' . $2}++;
	}
  if (/java.io.FileNotFoundException: (.*)$/) {
    $z->{$1}++;
  }
  if (/TEXTURE ERRORS.*(textures\/.*)$/) {
    $e->{$1}++;
  }
  if (/has localized name (.*?)\r?$/) {
    $l->{$1}++;
  }
}

close F;

open F, '>', '/f/github/manual/EnderIO_111/doc/missing.txt';

print F "Erroring Registry Names:\n";
print F join "\n", sort keys %$x;
print F "\n\n\n\n\n\n\n\nAll erroring blockstates:\n";
print F join "\n", sort grep $y->{$_}, keys %$y;
print F "\n\n\n\n\n\n\n\nSimple items:\n";
print F join "\n", sort grep /#inventory/, keys %$y;
print F "\n\n\n\n\n\n\n\nFiles not found:\n";
print F join "\n", sort keys %$z;
print F "\n\n\n\n\n\n\n\nMissing Texture:\n";
print F join "\n", sort keys %$e;

print F "\n\n\n\n\n\n\n\nGenerated blockstates:\n\n";
for my $rl (sort keys %$x) {
	my $name = $rl; $name =~ s/^.*://;
	print F qq!
blockstates/$name.json
{
  "forge_marker": 1,
  "defaults": {
    "model": "enderio:item_generated"
  },
  "variants": {
    "variant": {
      !;
	print F join ",
      ", map { /variant=(.*)/; qq!"$1": {
        "textures": {
          "layer0": "enderio:items/${name}_$1"
        }
      }!} sort keys %{$x->{$rl}};
  print F "
    }
  }
}

";
}

print F "\n\n\n\n\n\n\n\nLocalized names:\n";
print F map {$_."=\n"} sort keys %$l;

close F;

