package crazypants.enderio.machine.generator.zombie;

public enum GeneratorType {

	ZOMBIE("enderio:models/ZombieJar.png"),
	FRANKENZOMBIE("enderio:models/FrankenzombieJar.png"),
	ENDER("enderio:models/EnderJar.png");

	String texturePath;

	GeneratorType(String texture){
		this.texturePath = texture;
	}


	public String getTexture(){
		return texturePath;
	}
}
