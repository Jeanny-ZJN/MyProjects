import java.util.Scanner;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;

public class CountryDisplayer {

    private List<Country> countryList;
    private List<Country> countryOrder;
    private static int indicator;
    private static int indicator2;
    private static boolean greatestToLeast;


  public CountryDisplayer() {
      countryList = new ArrayList<>();
      countryOrder = new ArrayList<>();
      this.indicator = indicator;
      this.indicator2 = indicator2;
      this.greatestToLeast = greatestToLeast;
  }
 
 /**This method convert CountryDataset.csv to a string arraylist. It takes in a line from the document and add each part into the list in order.**/

  public List<Country> convertCountryList(String filename) {
      try {
      Scanner scanner = new Scanner(new File(filename));
      scanner.nextLine();
      while (scanner.hasNext()) {
          String next = scanner.nextLine();
          String[] countryData = next.split(",");
          Country list = new Country(countryData[0],countryData[1],countryData[2],countryData[3],countryData[4],countryData[5],countryData[6],countryData[7]);
          countryList.add(list);
          
      }
      } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        }
      //System.out.println("it worked!!!");
      return countryList;
  }

/**This method sort the list in either from largest to least or least to largest order (boolean) by comparing the data of interest (indicator)**/

  public List<Country> displayTextCountry(int indicator, boolean greatestToLeast) {
      if (greatestToLeast) {
        while(!countryList.isEmpty()){
          double max = 0;
          int maxIndex = 0;
          double data = 0;
          for (int i = 1; i < countryList.size(); i++){
              String doubleData = countryList.get(i).getData(indicator);
              data = Double.parseDouble(doubleData);
              if (data > max) {
                  max = data;
                  maxIndex = i;
              }
          }
          countryOrder.add(countryList.remove(maxIndex));
        }
      }
      else {
        while(!countryList.isEmpty()){
          int minIndex = 0;
          double min = 0;
          double data = 0;
          for (int i = 1; i < countryList.size(); i++) {
              String doubleData = countryList.get(i).getData(indicator);
              data = Double.parseDouble(doubleData);
              if (data < min) {
                  min = data;
                  minIndex = i;
              }
          }
          countryOrder.add(countryList.remove(minIndex));
        }
      }
    return countryOrder;
  }

/**I created this method so everything will not be clustered in main. This method return String name of each data.**/

  public String getString(int num) {
      String dataType = " ";
      if (num == 1) {
          return "CO2Emission";
      }
      else if (num == 2) {
          return "accessToElectricity";
      }
      else if (num == 3) {
          return "renewableEnergyConsumption";
      }
      else if (num == 4) {
          return "terrestrialProtectedAreas";
      }
      else if (num == 5) {
          return "populationGrowth";
      }
      else if (num == 6) {
          return "totalPopulation";
      }
      else if (num == 7) {
          return "urbanPopulationGrowth";
      }
      return dataType;
  }

  /**Did not use this method because all of my variables and methods are defined in main. I cannot figure out how to reference them in a separate method. Sorry about the huge cluster in main.**/ 

  //public JFreeChart displayCountryGraph(String datatype, String datatype2, List<String> listname, List<String> listname2, int indication, int indication2) {
  //     BarChart chart = new BarChart("Top 10 countries in " + datatype + " and " + datatype2, "Country", "Value");

  //     for (int i = 0; i < 10; i++) {
  //         chart.addValue(listname.get(i).getData(0),Double.parseDouble(listname.get(i).getData(indication)),getString(indication));

  //         chart.addValue(listname2.get(i).getData(0),Double.parseDouble(listname2.get(i).getData(indication2)),getString(indication2));
  //         }
  //     chart.displayChart();
  // }
  

  
  public static void main(String[] args) {

      String filename = args[0];
      String dataType = args[1];
      String maxOrMin = args[2];
      //String dataType2 = args[3];
      //boolean greatestToLeast;

      //int indicator = 0;
      if (args[1].equals("CO2Emission")) {
         indicator = 1;
      }
      else if (args[1].equals("accessToElectricity")) {
          indicator = 2;
      }
      else if (args[1].equals("renewableEnergyConsumption")) {
          indicator = 3;
      }
      else if (args[1].equals("terrestrialProtectedAreas")) {
          indicator = 4;
      }
      else if (args[1].equals("populationGrowth")) {
          indicator = 5;
      }
      else if (args[1].equals("totalPopulation")) {
          indicator = 6;
      }
      else if (args[1].equals("urbanPopulationGrowth")) {
          indicator = 7;
      }

      if (args[2].equals("greatestToLeast")) {
          greatestToLeast = true;
      }
      else if (args[2].equals("leastToGreatest")){
          greatestToLeast = false;
      }

      CountryDisplayer test = new CountryDisplayer();

      test.convertCountryList(filename);
      List<Country> countryRank = new ArrayList<Country>(); 
      countryRank = test.displayTextCountry(indicator, greatestToLeast);

      if (args.length == 3) {
          for (int i = 0; i < 10; i++) {
              countryRank.get(i).printAllData();
          }
      }
      
      else if (args.length == 4) {
          String dataType2 = args[3];
          BarChart chart = new BarChart("Top 10 countries in " + dataType + " and " + dataType2, "Country", "Value");
          
          //int indicator2 = 0;
          if (args[3].equals("CO2Emission")) {
              indicator2 = 1;
          }
          else if (args[3].equals("accessToElectricity")) {
              indicator2 = 2;
          }
          else if (args[3].equals("renewableEnergyConsumption")) {
              indicator2 = 3;
          }
          else if (args[3].equals("terrestrialProtectedAreas")) {
              indicator2 = 4;
          }
          else if (args[3].equals("populationGrowth")) {
              indicator2 = 5;
          }
          else if (args[3].equals("totalPopulation")) {
              indicator2 = 6;
          }
          else if (args[3].equals("urbanPopulationGrowth")) {
              indicator2 = 7;
          }

          List<Country> countryRank2 = new ArrayList<Country>(); 
          countryRank2 = test.displayTextCountry(indicator2, greatestToLeast);

          for (int i = 0; i < 10; i++) {
              chart.addValue(countryRank.get(i).getData(0),Double.parseDouble(countryRank.get(i).getData(indicator)),test.getString(indicator));

              chart.addValue(countryRank2.get(i).getData(0),Double.parseDouble(countryRank2.get(i).getData(indicator2)),test.getString(indicator2));
          }
          //chart.displayCountryGraph(dataType, dataType2,countryRank,countryRank2, indicator, indicator2);
          chart.displayChart();
      }
  }
}