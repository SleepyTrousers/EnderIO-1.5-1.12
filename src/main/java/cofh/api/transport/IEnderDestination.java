package cofh.api.transport;

public interface IEnderDestination extends IEnderAttuned {

	public boolean isNotValid();

	public int x();

	public int y();

	public int z();

	public int dimension();

	public int getDestination();

	public boolean setDestination(int frequency);

	public boolean clearDestination();

}
