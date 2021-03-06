/*
 * Copyright (c) 2011-2015 EPFL DATA Laboratory
 * Copyright (c) 2014-2015 The Squall Collaboration (see NOTICE)
 *
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package ch.epfl.data.squall.data_extractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.data.squall.utilities.MyUtilities;

public class GenerateOperatorLatency {
	private class FileLatency implements Comparable<FileLatency> {
		private String _workerId;
		private Double _latency;
		private long _tuples;

		public FileLatency(String workerId, double latency, long tuples) {
			_workerId = workerId;
			_latency = latency;
			_tuples = tuples;
		}

		@Override
		public int compareTo(FileLatency fl) {
			return _latency.compareTo(fl._latency);
		}

		public double getLatency() {
			return _latency;
		}

		public long getTuples() {
			return _tuples;
		}

		public Double getWeightedLatency() {
			return _tuples * _latency;
		}

		public String getWorkerId() {
			return _workerId;
		}

		public void setLatency(double _latency) {
			_latency = _latency;
		}

		public void setTuples(long _tuples) {
			this._tuples = _tuples;
		}

		public void setWorkerId(String _workerId) {
			_workerId = _workerId;
		}
	}

	private static String getLegend(int algSizes) {
		if (algSizes == STATIC_NAIVE) {
			return "StaticMid";
		} else if (algSizes == STATIC_OPT) {
			return "StaticOpt";
		} else if (algSizes == DYNAMIC) {
			return "Dynamic";
		} else {
			throw new RuntimeException("Developer error!");
		}
	}

	public static void main(String[] args) throws IOException {
		GenerateOperatorLatency generator = new GenerateOperatorLatency();
		String fullPath = working
				+ "cyclone_res/R7_squall_latency/squall_1k_store/";
		String avgLatency = "";

		/*
		 * FileOutputStream fos = new FileOutputStream(resultPath);
		 * BufferedOutputStream x = new BufferedOutputStream(fos);
		 * OutputStreamWriter out = new OutputStreamWriter(x);
		 * 
		 * //the order is important out.write("Query," +
		 * getLegend(STATIC_NAIVE)+ "," + getLegend(DYNAMIC) + "," +
		 * getLegend(STATIC_OPT)+"\n");
		 * 
		 * //Q5 out.write(THETA_TPCH5 + ","); avgLatency =
		 * String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_z4_static_naive_theta_tpch5_R_N_S_L")); out.write(avgLatency +
		 * ","); avgLatency = String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_z4_dynamic_theta_tpch5_R_N_S_L")); out.write(avgLatency + ",");
		 * avgLatency = String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_z4_static_opt_theta_tpch5_R_N_S_L")); out.write(avgLatency +
		 * "\n");
		 * 
		 * //Q7 out.write(THETA_TPCH7 + ","); avgLatency =
		 * String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_z4_static_naive_theta_tpch7_L_S_N1")); out.write(avgLatency +
		 * ","); avgLatency = String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_z4_dynamic_theta_tpch7_L_S_N1")); out.write(avgLatency + ",");
		 * avgLatency = String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_z4_static_opt_theta_tpch7_L_S_N1")); out.write(avgLatency +
		 * "\n");
		 * 
		 * //BNCI out.write(BAND_INPUT + ","); avgLatency =
		 * String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_uniform_static_naive_bnci")); out.write(avgLatency + ",");
		 * avgLatency = String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_uniform_dynamic_bnci")); out.write(avgLatency + ",");
		 * avgLatency = String.valueOf(generator.getAvgLatency(fullPath +
		 * "/10G_uniform_static_opt_bnci")); out.write(avgLatency + "\n");
		 * 
		 * out.close(); x.close(); fos.close();
		 */

		// only printing to standard out
		String path = working
				+ "cyclone_res/R7_squall_latency/dynamic_storm_16K_store_less_src/";
		List<String> suffixes = new ArrayList<String>();

		File dir = new File(path);
		if (dir.isDirectory()) {
			File[] listOfFiles = dir.listFiles();
			for (File file : listOfFiles) {
				if (file.isDirectory()) {
					String suffix = file.getName();
					if (!suffix.equals("cluster")) {
						suffixes.add(suffix);
					}
				}
			}
		} else {
			throw new RuntimeException(path + " is not a directory!");
		}

		for (String suffix : suffixes) {
			avgLatency = String.valueOf(generator.getAvgLatency(path + "/"
					+ suffix));
			System.out.println("\n\nAverage latency for " + suffix + " is "
					+ avgLatency + ".");
		}

	}

	private static String working = "experiments/Dropbox/";
	private static String resultPath = "VLDBPaperLatex/Results/csv/latency/squall_latency.csv";
	private static final double TOP_RESULTS = 0.74; // the relative TOP latency
													// files are analyzed

	private static int STATIC_NAIVE = 0;
	private static int STATIC_OPT = 1;
	private static int DYNAMIC = 2;
	private static final String BAND_INPUT = "BNCI";

	private static final String BAND_OUTPUT = "BCI";

	private static final String THETA_TPCH5 = "Q5";

	private static final String THETA_TPCH7 = "Q7";

	private static final long INVALID = -1;

	private double getAvgLatency(String dirPath) throws IOException {
		File f = new File(dirPath);
		if (!f.isDirectory()) {
			throw new RuntimeException("The path does not exist: " + dirPath);
		}

		System.out.println("\n\n\n DIRECTORY " + dirPath);
		List<FileLatency> fileLatencies = getFileLatencies(dirPath);
		Collections.sort(fileLatencies);

		double totalLatency = 0;
		long numTuples = 0;
		int size = fileLatencies.size();
		int cutSize = (int) (TOP_RESULTS * size);
		System.out.println("There are " + size
				+ " workers which reported on latency." + " We pick " + cutSize
				+ " of them with minimal latency.");

		for (int i = 0; i < cutSize; i++) {
			FileLatency current = fileLatencies.get(i);
			totalLatency += current.getWeightedLatency();
			numTuples += current.getTuples();
		}

		return totalLatency / numTuples;
	}

	private List<FileLatency> getFileLatencies(String dirPath)
			throws IOException {
		List<FileLatency> fileLatencies = new ArrayList<FileLatency>();
		for (int i = 1; i < 11; i++) {
			for (int j = 1; j < 23; j++) {

				String inpathss = i + "-1";
				if (j < 10) {
					inpathss += "00";
				} else {
					inpathss += "0";
				}
				inpathss += j;

				String suffix = "supervisor" + inpathss + "/worker-6700.log";
				String fullPath = dirPath + "/logs/" + suffix;
				File f = new File(fullPath);
				if (f.exists()) {
					double latency = getNodeLatency(fullPath);
					if (latency != INVALID) {
						long tuples = getSentTuples(fullPath);
						System.out.println("File " + suffix + " has latency "
								+ latency + "ms. It sent " + tuples
								+ " tuples.");
						FileLatency fl = new FileLatency(suffix, latency,
								tuples);
						fileLatencies.add(fl);
					}
				}
			}
		}
		return fileLatencies;
	}

	private double getNodeLatency(String fullPath) throws IOException {
		List<String> lines = MyUtilities.readFileLinesSkipEmpty(fullPath);
		Collections.reverse(lines);

		double latency = INVALID;
		for (String line : lines) {
			if (line.contains("AVERAGE")) {
				String[] parts = line.split(" ");
				String latencyStr = new String(parts[10].replace("ms.", ""));
				latency = Double.valueOf(latencyStr);
				break;
			}
		}
		return latency;
	}

	private long getSentTuples(String fullPath) throws IOException {
		List<String> lines = MyUtilities.readFileLinesSkipEmpty(fullPath);
		Collections.reverse(lines);

		long result = INVALID;
		for (String line : lines) {
			if (line.contains("Sent Tuples")) {
				String[] parts = line.split(",");
				int size = parts.length;
				String resultStr = new String(parts[size - 1]);
				result = Long.valueOf(resultStr);
				break;
			}
		}
		if (result == INVALID) {
			throw new RuntimeException(
					"No information of number of sent tuples for " + fullPath);
		}
		return result;
	}

}