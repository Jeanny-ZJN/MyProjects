import java.util.List;
import java.util.ArrayList;


public class Country {

    private String countryName;
    private String CO2Emission;
    private String accessToElectricity;
    private String renewableEnergyConsumption;
    private String terrestrialProtectedAreas;
    private String populationGrowth;
    private String totalPopulation;
    private String urbanPopulationGrowth;

  public Country(String name, String CO2, String electricity, String consumption, String areas, String populationGrowth, String totalPopulation, String urbanGrowth) {

      this.countryName = name;
      this.CO2Emission = CO2;
      this.accessToElectricity = electricity;
      this.renewableEnergyConsumption = consumption;
      this.terrestrialProtectedAreas = areas;
      this.populationGrowth = populationGrowth;
      this.totalPopulation = totalPopulation;
      this.urbanPopulationGrowth = urbanGrowth;  
  }

  public String getData(int indicator) {
      String dataType = " ";
      if (indicator == 0) {
          return this.countryName;
      }
      else if (indicator == 1) {
          return this.CO2Emission;
      }
      else if (indicator == 2) {
          return this.accessToElectricity;
      }
      else if (indicator == 3) {
          return this.renewableEnergyConsumption;
      }
      else if (indicator == 4) {
          return this.terrestrialProtectedAreas;
      }
      else if (indicator == 5) {
          return this.populationGrowth;
      }
      else if (indicator == 6) {
          return this.totalPopulation;
      }
      else if (indicator == 7) {
          return this.urbanPopulationGrowth;
      }
      return dataType;
  }


  public void printAllData() {
      System.out.println(this.countryName + ", " + "CO2Emission: " + this.CO2Emission + ", " + "accessToElectricity: " + this.accessToElectricity + ", " + "renewableEnergyConsumption: " + this.renewableEnergyConsumption + ", " + "terrestrialProtectedAreas: " + this.terrestrialProtectedAreas + ", " + "populationGrowth: " + this.populationGrowth + ", " + "totalPopulation: " + this.totalPopulation + ", " + "urbanPopulationGrowth: " + this.urbanPopulationGrowth);
  }
}
