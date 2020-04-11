package com.gayagok.models;


public class LunchItem {
  public String date, menu = "", personnel, nutrient;

  public LunchItem(String date, String menu, String personnel, String nutrient, String kcal) {
    this.date = date;
    this.personnel = personnel;
    this.nutrient = nutrient;

    try {
      if(!menu.equals("급식정보가 없습니다.")){
        for (String allergy : menu.split(" ")) { //김치1.2. 밥1.2 이런식으로 나오는 음식을 자름
          if (allergy.contains(".")) {
            String d = "";
            String[] allergies = allergy.split("\\.");
            for (int i = 1; i < allergies.length; i++) {
              d += "," + allergies[i];
            }

            if (isNumber(allergies[0].substring(allergies[0].length() - 2))) {
              this.menu += "<font color=#212121>" + allergies[0].substring(0, allergies[0].length() - 2) + "</font><font color=#757575>" + allergies[0].substring(allergies[0].length() - 2) + d + "</font><br>";
            } else {
              this.menu += "<font color=#212121>" + allergies[0].substring(0, allergies[0].length() - 1) + "</font><font color=#757575>" + allergies[0].substring(allergies[0].length() - 1) + d + "</font><br>";
            }

          } else {
            this.menu += "<font color=#212121>" + allergy + "</font><br>";
          }
        }
        this.menu += "<br><font color=#212121> "+kcal+" kcal</font><br>";
      } else {
        this.menu = "<font color=#212121>급식정보가 없습니다.</font><br>";
      }
    } catch (Exception e){

    }
  }

  private boolean isNumber(String str) {
    boolean result = false;
    try {
      Double.parseDouble(str);
      result = true;
    } catch (Exception e) {
    }
    return result;
  }

}
