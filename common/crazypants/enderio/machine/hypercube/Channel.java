package crazypants.enderio.machine.hypercube;

public class Channel {

  final String name;
  final String user;
  
  Channel(String name, String user) {  
    this.name = trim(name);    
    this.user = trim(user);    
  }
  
  public boolean isPublic() {
    return user == null;
  }
  
  private String trim(String str) {
    if(str == null) {
      return null;
    }
    str = str.trim();
    if(str.isEmpty()) {
      return null;
    }
    return str;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Channel other = (Channel) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (user == null) {
      if (other.user != null)
        return false;
    } else if (!user.equals(other.user))
      return false;
    return true;
  }
  
}
