package com.zyw.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Utils {
	
	public  static void  saveHtml(Configuration configuration,HttpServletRequest request,String htmlFileName,String modelName,Map content) {
		String htmlRealPath=request.getSession().getServletContext().getRealPath("/")+"\\html\\";
		System.out.println("保存的绝对路径是:"+htmlRealPath+ "/" + htmlFileName + ".html");
		 File htmlFile = new File(htmlRealPath + "/" + htmlFileName + ".html");
		 try {
		 if (!htmlFile.exists()) {
	            // 获得模板对象
	            Template template = configuration .getTemplate(modelName+".ftl");

	            //先得到文件的上级目录，并创建上级目录，在创建文件
	            htmlFile.getParentFile().mkdir();
	           
	                //创建文件
	                htmlFile.createNewFile();
		            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8"));
		            // 合并输出 创建页面文件
		            template.process(content,out);
		            out.flush();
		            out.close();
	        }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (TemplateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


	}

}
