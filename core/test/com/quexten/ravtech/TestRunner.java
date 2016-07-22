package com.quexten.ravtech;

import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.net.kryonet.LayerTest;

public class TestRunner {

	static Array<Test> tests = new Array<Test>();
	
	public static void main(String[] args) {
		tests.add(new LayerTest());
		runTests(tests);
	}
	
	public static void runTests(Array<Test> tests) {
		for(int i = 0; i < tests.size; i++) {
			System.out.println("Running Test:" + tests.get(i).getClass().getSimpleName());
			System.out.println(tests.get(i).test() ? "Successful!" : "Error");
		}
	}
	
}
