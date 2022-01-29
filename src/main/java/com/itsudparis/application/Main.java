/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.itsudparis.application;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.ResultSet;
import com.opencsv.CSVReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author DO.ITSUDPARIS
 */
public class Main {

	/**
	 * @param args rhe command line arguments
	 * @throws FileNotFoundException
	 */

	public static void main(String[] args) throws FileNotFoundException {
		String NS = "";
		// lire le model a partir d'une ontologie
		Model model = JenaEngine.readModel("data/iottp.owl");
		if (model != null) {
			NS = model.getNsPrefixURI("");
			String csvFilename = "data/row.csv";
			try {
				CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
				String[] csvrow = null;
				int i = 0;
				while ((csvrow = csvReader.readNext()) != null) {
					i++;
					if (i == 1) {
						continue;
					} else if (i > 100) {
						break;
					}
					JenaEngine.createInstanceOfClass(model, NS, "Mesure", csvrow[17]);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "measurement_timestamp",
							csvrow[1].length() != 0 ? csvrow[1] : "0");
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "air_temperature",
							csvrow[2].length() != 0 ? Float.parseFloat(csvrow[2]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "wet_bulb_temperature",
							csvrow[3].length() != 0 ? Float.parseFloat(csvrow[3]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "humidity",
							csvrow[4].length() != 0 ? Float.parseFloat(csvrow[4]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "rain_intensity",
							csvrow[5].length() != 0 ? Float.parseFloat(csvrow[5]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "total_rain",
							csvrow[7].length() != 0 ? Float.parseFloat(csvrow[7]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "wind_direction",
							csvrow[9].length() != 0 ? Integer.parseInt(csvrow[9]) : 0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "wind_speed",
							csvrow[10].length() != 0 ? Float.parseFloat(csvrow[10]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "maximum_wind_speed",
							csvrow[11].length() != 0 ? Float.parseFloat(csvrow[11]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "barometric_pressure",
							csvrow[12].length() != 0 ? Float.parseFloat(csvrow[12]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "solar_radiation",
							csvrow[13].length() != 0 ? Integer.parseInt(csvrow[13]) : 0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "heading",
							csvrow[14].length() != 0 ? Integer.parseInt(csvrow[14]) : 0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "battery_life",
							csvrow[15].length() != 0 ? Float.parseFloat(csvrow[15]) : 0.0);
					JenaEngine.updateValueOfDataTypeProperty(model, NS, csvrow[17], "measurement_timestamp",
							csvrow[16].length() != 0 ? csvrow[16] : "void");

				}
			} catch (Exception e) {
				System.out.println("exception :" + e.getMessage());
			}
			// modifier le model
			// Ajouter une nouvelle femme dans le modele: Nora,50, estFilleDe Peter
			/*
			 * JenaEngine.createInstanceOfClass(model, NS, "Femme","Nora");
			 * //JenaEngine.createInstanceOfClass(model, NS, "Personne","Nora");
			 * JenaEngine.updateValueOfDataTypeProperty(model, NS,"Nora", "age", 50);
			 * JenaEngine.updateValueOfObjectProperty(model, NS,"Nora", "estFilleDe",
			 * "Peter");
			 * // Ajouter un nouvel homme dans le modele: Rob, 51,seMarierAvec Nora
			 * JenaEngine.createInstanceOfClass(model, NS, "Homme","Rob");
			 * JenaEngine.updateValueOfDataTypeProperty(model, NS,"Rob", "age", 51);
			 * JenaEngine.updateValueOfDataTypeProperty(model, NS,"Rob", "nom",
			 * "Rob Yeung");
			 * JenaEngine.updateValueOfObjectProperty(model, NS,"Rob", "seMarierAvec",
			 * "Nora");
			 */

			Model owlInferencedModel = JenaEngine.readInferencedModelFromRuleFile(model, "data/owlrules.txt");
			// apply our rules on the owlInferencedModel
			Model inferedModel = JenaEngine.readInferencedModelFromRuleFile(owlInferencedModel, "data/rules.txt");
			// query on the model after inference
			System.out.println(JenaEngine.executeQueryFile(inferedModel, "data/query.txt"));
		} else {
			System.out.println("Error when reading model from ontology");
		}
		ArrayList<String> listeRep = new ArrayList<>();
		HashMap<String, String> mapp = new HashMap<String, String>();
		Scanner myObj = new Scanner(System.in);
		System.out.println("La plage chicago est elle faites pour vous ?");
		for (int i = 0; i <= 4; i++) {
			switch (i) {
				case 0:
					System.out.println("Vous voulez une temperature (de l'air) de combien de degre en (C°)?");
					mapp.put("air_temperature", myObj.nextLine());
					break;
				case 1:
					System.out.println("Le taux d'humidité ?");
					mapp.put("humidity", myObj.nextLine());
					break;
				case 2:
					System.out.println("Voulez vous de la pluie ? (oui ou non)");
					mapp.put("rain_intensity", myObj.nextLine());
					break;
				case 3:
					System.out.println("Voulez vous du vent ? (oui ou non)");
					mapp.put("wind_speed", myObj.nextLine());
					break;
				case 4:
					System.out.println("Voulez vous du soleil ?(oui,non ou un peu) ");
					mapp.put("solar_radiation", myObj.nextLine());
					break;
				default:
					break;
			}
		}
		String prefix = "PREFIX ns: <http://www.semanticweb.org/ilyes/ontologies/2022/0/iot_tp#> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
		for (String key : mapp.keySet()) {
			String q = String.format("SELECT ?t ?v WHERE { ?t  ns:%s ?v}", key);
			Query query = QueryFactory.create(q);
			QueryExecution qe = QueryExecutionFactory.create(prefix + query, model);
			com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode t = soln.get("t");
				RDFNode v = soln.get("v");
				System.out.println(t + " " + v);
			}

		}
	}
}
