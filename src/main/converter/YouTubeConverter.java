package main.converter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.github.axet.vget.VGet;

public class YouTubeConverter {

	public static void convert(String url) {
    String path = ".";
    try {
    	System.out.println("start downloading...");
    	VGet v = new VGet(new URL(url), new File(path));
    	v.download();
    } catch(MalformedURLException e) {
    	System.out.println(e.getMessage());
    }
	}

}
