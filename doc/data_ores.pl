our $ores = [

	{
		name => 'Iron', requ => 'true',

		dust => 'POWDER_IRON', amount => 2,
		extra => [
			[ 'dustTin', 1, 0.05, 'false' ],
			[ 'dustNickel', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Gold', requ => 'true',

		dust => 'dust*', amount => 2,
		extra => [
			[ 'dustCopper', 1, 0.2, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Copper', requ => 'false',

		dust => 'dust*', amount => 2,
		extra => [
			[ 'dustGold', 1, 0.125, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Tin', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Lead', requ => 'false',

		dust => 'dust*', amount => 2,
		extra => [
			[ 'dustSilver', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Silver', requ => 'false',

		dust => 'dust*', amount => 2,
		extra => [
			[ 'dustLead', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Nickel', requ => 'false',

		dust => 'dust*', amount => 2,
		extra => [
			[ 'dustPlatinum', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Aluminium', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Aluminum', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'NaturalAluminum', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Coal', requ => 'true',

		dust => 'minecraft:coal', amount => 3,
		extra => [
			[ 'dustCoal', 1, 0.6, 'true' ],
			[ 'gemDiamond', 1, 0.001, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/3, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Redstone', requ => 'true',

		dust => 'dust*', amount => 8,
		extra => [
			[ 'dustRedstone', 1, 0.2, 'true' ],
			[ 'itemSilicon', 1, 0.8, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 21/8, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Diamond', requ => 'true',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gemDiamond', 1, 0.25, 'true' ],
			[ 'dustCoal', 1, 0.05, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Emerald', requ => 'true',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gemEmerald', 1, 0.25, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Lapis', requ => 'true',

		dust => 'minecraft:dye:4', amount => 8,
		extra => [
			[ 'minecraft:dye:4', 1, 0.2, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 3, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Quartz', requ => 'true',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'POWDER_QUARTZ', 1, 0.1, 'true' ],
		],
		base => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Apatite', mod => 'Forestry', requ => 'false',

		dust => 'gem*', amount => 12,
		extra => [
			[ 'dustSulfur', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'CertusQuartz', mod => 'AE2', requ => 'false',

		ore => 'appliedenergistics2:quartz_ore', # sic! oreChargedCertusQuartz is oredicted to oreCertusQuartz
		dust => 'crystal*', amount => 2,
		extra => [
			[ 'dustCertusQuartz', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'ChargedCertusQuartz', mod => 'AE2', requ => 'false',

		dust => 'appliedenergistics2:material:1', amount => 2,
		extra => [
			[ 'dustCertusQuartz', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Sulfur', mod => 'Railcraft', requ => 'false',

		dust => 'dust*', amount => 6,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 4, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Saltpeter', mod => 'Railcraft', requ => 'false',

		dust => 'dust*', amount => 4,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 10/4, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Ruby', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Peridot', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Topaz', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Tanzanite', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Malachite', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Sapphire', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Amber', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Amethyst', mod => 'BoP', requ => 'false',

		dust => 'gem*', amount => 2,
		extra => [
			[ 'gem*', 1, 0.5, 'true' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 5/2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Manganese', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Zinc', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Platinum', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Ignatius', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'ShadowIron', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Lemurite', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Midasium', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Vyroxeres', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Ceruclase', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Kalendrite', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Vulcanite', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Sanguinite', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Prometheum', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'DeepIron', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Infuscolium', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Oureclase', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'AstralSilver', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Carmot', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Mithril', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Rubracium', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Orichalcum', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Adamantine', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Atlarus', mod => 'Metallurgy', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Osmium', mod => 'Mekanism', requ => 'false',

		dust => 'dust*', amount => 2,
		extra => [
			[ 'dust*', 1, 0.1, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Yellorite', mod => 'Tiny Reactors', requ => 'false',

		dust => 'dustYellorium', amount => 2,
		extra => [
			[ 'dustCyanite', 1, 0.05, 'false' ],
		],
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingotYellorium', block => 'blockYellorium',
	},

	{
		name => 'Ardite', mod => 'TiC', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Cobalt', mod => 'TiC', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'QuartzBlack', mod => 'AA', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,
	},

	{
		name => 'Uranium', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Steel', requ => 'false',

		dust => 'dust*',

		netheramount => 4, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Titanium', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Magnesium', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Tungsten', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Rutile', requ => 'false',

		dust => 'dust*', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingot*', block => 'block*',
	},

	{
		name => 'Salt', requ => 'false',

		dust => 'dust*',

		netheramount => 12, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Draconic', mod => 'Draconic', requ => 'false',

		ore => 'draconicevolution:draconium_ore:0',
		dust => 'dustDraconic', amount => 2,
		base => [ 'oredict:cobblestone', 1, 0.15 ],

		netheramount => 2, # *amount
		netherbase => [ 'minecraft:netherrack', 1, 0.15 ],

		denseamount => 4,

		ingot => 'ingotDraconic', block => 'blockDraconic',
	},

	{
		name => 'Draconic', mod => 'Draconic', requ => 'false',

		ore => 'draconicevolution:draconium_ore:1',
		dust => 'dustDraconic', amount => 2,
		base => [ 'minecraft:netherrack', 1, 0.15 ],
	},

	{
		name => 'Draconic', mod => 'Draconic', requ => 'false',

		ore => 'draconicevolution:draconium_ore:2',
		dust => 'dust*', amount => 2,
		base => [ 'minecraft:end_stone', 1, 0.15 ],
	},

  {
    name => 'Prosperity', mod => 'Mystical Agriculture', requ => 'false',

    dust => 'shard*', amount => 4,
    base => [ 'oredict:cobblestone', 1, 0.15 ],
  },

  {
    name => 'NetherProsperity', mod => 'Mystical Agriculture', requ => 'false',

    dust => 'shardProsperity', amount => 4,
    base => [ 'minecraft:netherrack', 1, 0.15 ],
  },

  {
    name => 'EndProsperity', mod => 'Mystical Agriculture', requ => 'false',

    dust => 'shardProsperity', amount => 4,
    base => [ 'minecraft:end_stone', 1, 0.15 ],
  },

  {
    name => 'Inferium', mod => 'Mystical Agriculture', requ => 'false',

    dust => 'essence*', amount => 4,
    base => [ 'oredict:cobblestone', 1, 0.15 ],
  },

  {
    name => 'NetherInferium', mod => 'Mystical Agriculture', requ => 'false',

    dust => 'essenceInferium', amount => 4,
    base => [ 'minecraft:netherrack', 1, 0.15 ],
  },

  {
    name => 'EndInferium', mod => 'Mystical Agriculture', requ => 'false',

    dust => 'essenceInferium', amount => 4,
    base => [ 'minecraft:end_stone', 1, 0.15 ],
  },



];

