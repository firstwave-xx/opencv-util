package com.echoss.opencv310.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
//import java.util.Scanner;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/*
 * Only implement CSV <-> opencv Mat read and write.
 * 
 * 
 * 
 * 
 * 
 */
public class CSVFileStorage {
	// static
	public static final int READ = 0;
	public static final int WRITE = 1;
	
	// varaible
	private File file;
	private boolean isWrite;
	private List<String> titles = new ArrayList<String>();
	private List<String> types = new ArrayList<String>();
	private List<String> models = new ArrayList<String>();
	
	private List<Integer> typeList = new ArrayList<Integer>();
	private List<Integer> modelList = new ArrayList<Integer>();
	private List<float[]> dataList = new ArrayList<float[]>();
	
	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public List<String> getModels() {
		return models;
	}

	public void setModels(List<String> models) {
		this.models = models;
	}

	public List<Integer> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<Integer> typeList) {
		this.typeList = typeList;
	}

	public List<Integer> getModelList() {
		return modelList;
	}

	public void setModelList(List<Integer> modelList) {
		this.modelList = modelList;
	}

	public List<float[]> getDataList() {
		return dataList;
	}

	public void setDataList(List<float[]> dataList) {
		this.dataList = dataList;
	}

	
	public CSVFileStorage() {
		file = null;
		isWrite = false;
	}
	
	public CSVFileStorage(String filePath) {
		file = null;
		isWrite = false;
		open(filePath, READ);
	}
	
	
	// clear
	public void clear() {
		titles.clear();
		types.clear();
		models.clear();
		typeList.clear();
		modelList.clear();
		dataList.clear();
	}
	
	
	// read only
	public void open(String filePath, int flags ) {
		try {
			if( flags == READ ) {
				read(filePath);
			}
			else {
				create(filePath);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	
	// read only
	public void read(String filePath) {
		try {
			file = new File(filePath);
			if( file == null || file.isFile() == false ) {
				System.err.println("Can not open file: " + filePath );
			}
			else {
				isWrite = false;
				parseFile(file);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseFile(File file) {
		final String DELIMETER = ",";
		
		int lines = 0;
		int dataSize = 0;
		BufferedReader br = null;
		
		clear();
		
		try {
			br = new BufferedReader(new FileReader(file));
			String lineData = null;
			while((lineData = br.readLine()) != null) {
				if(lines == 0) {	// title
					String ts[] = lineData.split(DELIMETER);
					for( int i=0; i<ts.length ;i++) {
						titles.add(ts[i]);
						dataSize = ts.length - 2;
					}
				}
				else {
					String ss[] = lineData.split(DELIMETER);
					int type = findIndex(types, ss[0]);
					typeList.add(type);
					
					int model = findIndex(models, ss[1]);
					modelList.add(model);
					
					float[] data = new float[dataSize];
					for( int i=2; i<ss.length; i++) {
						data[i-2] = Float.parseFloat(ss[i]);
					}
					dataList.add(data);
				}
				
				lines += 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private int findIndex(List<String> list, String string) {
		int foundIndex = -1;
		for( int i=0 ; i<list.size() ; i++) {
			if( list.get(i).equals(string) ) {
				foundIndex = i;
				break;
			}
		}
		
		if( foundIndex < 0 ) {
			foundIndex = list.size();
			list.add( string );
		}
		return foundIndex;
	}
	
	
	// write only
	public void create(String filePath) {
		try {
			file = new File(filePath);
			if( file == null ) {
				System.err.println("Can not wrtie file: " + filePath );
			}
			else {
				isWrite = true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public Mat CSV2MAT(String modelFilter, String dataType) {
		if( isWrite ) {
			System.err.println("Try read from file with write flags");
			return null;
		}
		
		int modelSize = models.size();
		int model = findIndex(models, modelFilter);
		if( model == modelSize ) {
			System.err.println("Try read unlisted phone model");
		}
		
		Mat readMat = null;
		
		if( "data".equals(dataType) ) {
			
			int rows = dataList.size();
			int cols = titles.size() - 2;
			int type = CvType.CV_32F;
			
			readMat = new Mat(rows, cols, type);
			
			for( int i=0 ; i<dataList.size(); i++ ) {
				if( modelList.get(i) == model )	{
					readMat.put(i, 0, dataList.get(i));
				}
			}
		}
		else if( "class".equals(dataType) ) {
			int rows = typeList.size();
			int cols = 1;
			int type = CvType.CV_32S;
			
			readMat = new Mat(rows, cols, type);
			
			int is[] = new int[1];
			for( int i=0 ; i<typeList.size(); i++ ) {
				if( modelList.get(i) == model )	{
					is[0] = (int)(typeList.get(i));
					readMat.put(i, 0, is);
				}
			}
		}
		return readMat;
	}
	
	

}
