#!/usr/bin/perl

use strict;
use warnings;

our $ores = [];

if (not $ARGV[0] or not -e $ARGV[0]) {
	die "Syntax: $0 <data file>";
}

require $ARGV[0];

our $template_ore = '
  <recipe name="Sagmill: ###NAME### Ore" required="###REQ###">
    <sagmilling energy="3600">
      <input name="###NAME###"/>
      <output name="###DUST###" amount="###AMOUNT###" />###EXTRA###
    </sagmilling>
  </recipe>
';
our $template_ore_extra = '
      <output name="###NAME###" amount="###AMOUNT###" chance="###CHANCE###" required="###REQ###" />';
our $template_ore_base = '
      <output name="###NAME###" amount="###AMOUNT###" chance="###CHANCE###" />';
our $template_nether_ore = '
  <recipe name="Sagmill: Nether ###NAME### Ore" required="false">
    <sagmilling energy="3200">
      <input name="oreNether###NAME###"/>
      <output name="###DUST###" amount="###AMOUNT###" />###EXTRA###
    </sagmilling>
  </recipe>
';
our $template_dense_ore = '
  <recipe name="Sagmill: Dense ###NAME### Ore" required="false">
    <sagmilling energy="4800">
      <input name="denseore###NAME###"/>
      <output name="ore###NAME###" amount="###AMOUNT###" />
    </sagmilling>
  </recipe>
';
our $template_ingot = '
  <recipe name="Sagmill: ###NAME### Ingot" required="###REQ###">
    <sagmilling energy="2400" bonus="none">
      <input name="###INGOT###"/>
      <output name="###DUST###" />
    </sagmilling>
  </recipe>

  <recipe name="Smelting: ###NAME### Dust" required="###REQ###">
    <smelting>
      <input name="###DUST###"/>
      <output name="###INGOT###"/>
    </smelting>
  </recipe>
';
our $template_block = '
  <recipe name="Sagmill: ###NAME### Block" required="###REQ###">
    <sagmilling energy="3600" bonus="none">
      <input name="###BLOCK###"/>
      <output name="###DUST###" amount="9" />
    </sagmilling>
  </recipe>
';
our $template_header = '
  <!-- ###NAME### -->
';

foreach my $ore (@$ores) {
	
	my $output = '';
	
	my $header = $template_header;
	if ($ore->{mod}) {
		$header =~ s/(###NAME###)/$1 ($ore->{mod})/g;
	}
	$header =~ s/###NAME###/$ore->{name}/g;
	$output .= $header;
	
	# (1) ore
	if ($ore->{dust} and $ore->{amount}) {
		my $s_ore = $template_ore;
		$s_ore =~ s/###NAME###/$ore->{ore} || 'ore'.$ore->{name}/ge;
		$s_ore =~ s/###REQ###/$ore->{requ}/g;
		$s_ore =~ s/###DUST###/mkName($ore->{name}, $ore->{dust})/ge;
		$s_ore =~ s/###AMOUNT###/$ore->{amount}/g;
		if ($ore->{extra}) {
			foreach my $extra (@{$ore->{extra}}) {
				my $s_extra = $template_ore_extra;
				$s_extra =~ s/###NAME###/mkName($ore->{name}, $extra->[0])/ge;
				$s_extra =~ s/###AMOUNT###/$extra->[1]/g;
				$s_extra =~ s/###CHANCE###/$extra->[2]/g;
				$s_extra =~ s/###REQ###/$extra->[3]/g;
				$s_extra =~ s/\s*required="true"\s*/ /g;
				$s_ore =~ s/(###EXTRA###)/$s_extra$1/g;
			}
		}
		if ($ore->{base}) {
				my $s_extra = $template_ore_base;
				$s_extra =~ s/###NAME###/$ore->{base}->[0]/ge;
				$s_extra =~ s/###AMOUNT###/$ore->{base}->[1]/g;
				$s_extra =~ s/###CHANCE###/$ore->{base}->[2]/g;
				$s_ore =~ s/(###EXTRA###)/$s_extra$1/g;
		}
		$s_ore =~ s/###EXTRA###//g;
		$output .= $s_ore;
	}
	
	# (2) nether ore
	if ($ore->{netheramount}) {
		my $s_ore = $template_nether_ore;
		$s_ore =~ s/###NAME###/$ore->{name}/g;
		$s_ore =~ s/###DUST###/mkName($ore->{name}, $ore->{dust})/ge;
		$s_ore =~ s/###AMOUNT###/int($ore->{netheramount} * ($ore->{amount} || 1))/ge;
		if ($ore->{extra}) {
			foreach my $extra (@{$ore->{extra}}) {
				my $s_extra = $template_ore_extra;
				$s_extra =~ s/###NAME###/mkName($ore->{name}, $extra->[0])/ge;
				$s_extra =~ s/###AMOUNT###/int($ore->{netheramount} * $extra->[1])/ge;
				$s_extra =~ s/###CHANCE###/$extra->[2]/g;
				$s_extra =~ s/###REQ###/$extra->[3]/g;
				$s_extra =~ s/\s*required="true"\s*/ /g;
				$s_ore =~ s/(###EXTRA###)/$s_extra$1/g;
			}
		}
		if ($ore->{netherbase}) {
				my $s_extra = $template_ore_base;
				$s_extra =~ s/###NAME###/$ore->{netherbase}->[0]/g;
				$s_extra =~ s/###AMOUNT###/$ore->{netherbase}->[1]/g;
				$s_extra =~ s/###CHANCE###/$ore->{netherbase}->[2]/g;
				$s_ore =~ s/(###EXTRA###)/$s_extra$1/g;
		}
		$s_ore =~ s/###EXTRA###//g;
		$output .= $s_ore;
	}
	
	# (3) dense ore
	if ($ore->{denseamount}) {
		my $s_ore = $template_dense_ore;
		$s_ore =~ s/###NAME###/$ore->{name}/g;
		$s_ore =~ s/###AMOUNT###/$ore->{denseamount}/g;
		$output .= $s_ore;
	}

	# (4) ingot <-> dust
	if ($ore->{dust} and $ore->{ingot}) {
		my $s_ore = $template_ingot;
		$s_ore =~ s/###NAME###/$ore->{name}/g;
		$s_ore =~ s/###REQ###/$ore->{requ}/g;
		$s_ore =~ s/###DUST###/mkName($ore->{name}, $ore->{dust})/ge;
		$s_ore =~ s/###INGOT###/mkName($ore->{name}, $ore->{ingot})/ge;
		$output .= $s_ore;
	}
	
	# (4) block -> 9 dust
	if ($ore->{dust} and $ore->{block}) {
		my $s_ore = $template_block;
		$s_ore =~ s/###NAME###/$ore->{name}/g;
		$s_ore =~ s/###REQ###/$ore->{requ}/g;
		$s_ore =~ s/###DUST###/mkName($ore->{name}, $ore->{dust})/ge;
		$s_ore =~ s/###BLOCK###/mkName($ore->{name}, $ore->{block})/ge;
		$output .= $s_ore;
	}
	
	$output =~ s/\s*amount="1"\s*/ /g;
	$output =~ s/\s*chance="1(.0)?"\s*/ /g;
	$output =~ s/\s*\/>/ \/>/g;
	$output =~ s/\s+>/>/g;
	
	print $output;
}

sub mkName {
	my $name = shift;
	my $template = shift;
	$template =~ s/\*/$name/g;
	return $template;
}