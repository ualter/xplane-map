package br.ujr.xplane.map;

import java.net.InetAddress;
import java.util.HashMap;

public class PlanesList
{
  private HashMap<String, Float> latMap = new HashMap();
  private HashMap<String, Float> lonMap = new HashMap();
  private HashMap<String, Float> altMap = new HashMap();
  
  public HashMap<String, Float> getLatMap()
  {
    return this.latMap;
  }
  
  public HashMap<String, Float> getLonMap()
  {
    return this.lonMap;
  }
  
  public HashMap<String, Float> getAltMap()
  {
    return this.altMap;
  }
  
  public boolean hasPlane(InetAddress ip)
  {
    return (this.latMap.containsKey(ip.toString())) && (this.lonMap.containsKey(ip.toString()));
  }
  
  public void setPlaneLat(InetAddress ip, float lat)
  {
    this.latMap.put(ip.toString(), Float.valueOf(lat));
  }
  
  public void setPlaneLon(InetAddress ip, float lon)
  {
    this.lonMap.put(ip.toString(), Float.valueOf(lon));
  }
  
  public void setPlaneAlt(InetAddress ip, float alt)
  {
    this.altMap.put(ip.toString(), Float.valueOf(alt));
  }
  
  public float[] getPlaneCoordinates(InetAddress ip)
    throws Exception
  {
    float[] result = new float[3];
    if (hasPlane(ip))
    {
      result[0] = getPlaneLat(ip);
      result[1] = getPlaneLon(ip);
      result[2] = getPlaneAlt(ip);
    }
    else
    {
      throw new Exception("Plane ip " + ip.toString() + "does not exist");
    }
    return result;
  }
  
  public float getPlaneLat(InetAddress ip)
  {
    return ((Float)this.latMap.get(ip.toString())).floatValue();
  }
  
  public float getPlaneLon(InetAddress ip)
  {
    return ((Float)this.lonMap.get(ip.toString())).floatValue();
  }
  
  public float getPlaneAlt(InetAddress ip)
  {
    return ((Float)this.altMap.get(ip.toString())).floatValue();
  }
}
