(ns app.db)

(def east
  [[{:name "Virginia" :seed 1  :ranking 1 :id "1001"} {:name "Colgate" :seed 2  :ranking 16 :id "1016"}]
   [{:name "Baylor" :seed 1  :ranking 8 :id "1008"} {:name "Ole Miss" :seed 2  :ranking 9 :id "1009"}]
   [{:name "Maryland" :seed 1  :ranking 5 :id "1005"} {:name "New Mexico State" :seed 2  :ranking 12 :id "1012"}]
   [{:name "Nevada" :seed 1  :ranking 4 :id "1004"} {:name "Yale" :seed 2  :ranking 13 :id "1013"}]
   [{:name "Buffalo" :seed 1  :ranking 6 :id "1006"} {:name "TCU" :seed 2  :ranking 11 :id "1011"}]
   [{:name "Purdue" :seed 1  :ranking 3 :id "1003"} {:name "Vermont" :seed 2  :ranking 14 :id "1014"}]
   [{:name "Louisville" :seed 1  :ranking 7 :id "1007"} {:name "UCF" :seed 2  :ranking 10 :id "1010"}]
   [{:name "Tennessee" :seed 1  :ranking 2 :id "1002"} {:name "Radford" :seed 2  :ranking 15 :id "1015"}]])

(def west
  [[{:name "Gonzaga" :seed 1  :ranking 1 :id "2001"} {:name "Prairie View A&M" :seed 2  :ranking 16 :id "2016"}]
   [{:name "Florida" :seed 1  :ranking 8 :id "2008"} {:name "Ohio State" :seed 2  :ranking 9 :id "2009"}]
   [{:name "Florida State" :seed 1  :ranking 5 :id "2005"} {:name "Arizona State" :seed 2  :ranking 12 :id "2012"}]
   [{:name "LSU" :seed 1  :ranking 4 :id "2004"} {:name "Old Dominion" :seed 2  :ranking 13 :id "2013"}]
   [{:name "Villanova" :seed 1  :ranking 6 :id "2006"} {:name "Alabama" :seed 2  :ranking 11 :id "2011"}]
   [{:name "Kansas" :seed 1  :ranking 3 :id "2003"} {:name "UC Irvine" :seed 2  :ranking 14 :id "2014"}]
   [{:name "Wofford" :seed 1  :ranking 7 :id "2007"} {:name "Oklahoma" :seed 2  :ranking 10 :id "2010"}]
   [{:name "Michigan" :seed 1  :ranking 2 :id "2002"} {:name "Montana" :seed 2  :ranking 15 :id "2015"}]])

(def midwest
  [[{:name "Kentucky" :seed 1  :ranking 1 :id "3001"} {:name "Iona" :seed 2  :ranking 16 :id "3016"}]
   [{:name "Syracuse" :seed 1  :ranking 8 :id "3008"} {:name "Washington" :seed 2  :ranking 9 :id "3009"}]
   [{:name "Virginia Tech" :seed 1  :ranking 5 :id "3005"} {:name "Belmont" :seed 2  :ranking 12 :id "3012"}]
   [{:name "Marquette" :seed 1  :ranking 4 :id "3004"} {:name "Hofstra" :seed 2  :ranking 13 :id "3013"}]
   [{:name "Kansas State" :seed 1  :ranking 6 :id "3006"} {:name "Minnesota" :seed 2  :ranking 11 :id "3011"}]
   [{:name "Houston" :seed 1  :ranking 3 :id "3003"} {:name "South Dakota State" :seed 2  :ranking 14 :id "3014"}]
   [{:name "Iowa" :seed 1  :ranking 7 :id "3007"} {:name "VCU" :seed 2  :ranking 10 :id "3010"}]
   [{:name "North Carolina" :seed 1  :ranking 2 :id "3002"} {:name "Loyola-Chicago" :seed 2  :ranking 15 :id "3015"}]])

(def south
  [[{:name "Duke" :seed 1  :ranking 1 :id "4001"} {:name "Sam Houston State" :seed 2  :ranking 16 :id "4016"}]
   [{:name "Auburn" :seed 1  :ranking 8 :id "4008"} {:name "Texas" :seed 2  :ranking 9 :id "4009"}]
   [{:name "Iowa State" :seed 1  :ranking 5 :id "4005"} {:name "Clemson" :seed 2  :ranking 12 :id "4012"}]
   [{:name "Wisconsin" :seed 1  :ranking 4 :id "4004"} {:name "Lipscomb" :seed 2  :ranking 13 :id "4013"}]
   [{:name "Mississippi State" :seed 1  :ranking 6 :id "4006"} {:name "St. Johns" :seed 2  :ranking 11 :id "4011"}]
   [{:name "Texas Tech" :seed 1  :ranking 3 :id "4003"} {:name "Texas State" :seed 2  :ranking 14 :id "4014"}]
   [{:name "Cincinatti" :seed 1  :ranking 7 :id "4007"} {:name "NC State" :seed 2  :ranking 10 :id "4010"}]
   [{:name "Michigan State" :seed 1  :ranking 2 :id "4002"} {:name "Northern Kentucky" :seed 2  :ranking 15 :id "4015"}]])

(defn bracket [region]
  [region
   [[]
    []
    []
    []]
   [[]
    []]
   [[]]])

(def default-db
  {:bracket {:east (bracket east)
             :west (bracket west)
             :midwest (bracket midwest)
             :south (bracket south)}})
