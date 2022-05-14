package br.com.leandrocolevati.JasperReportsF12014v1.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
public class RelatorioJSONController {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(name = "relatoriojson", value = "/relatoriojson", method = RequestMethod.POST)
	public ResponseEntity geraRelatorio(@RequestParam Map<String, String> allRequestParams) {
		String erro = "";
		
		//Definindo os elementos que serão passas como parâmetros para o Jasper
		Map<String, Object> param = new HashMap<String, Object>();
		
		byte[] bytes = null;
		
		//Inicializando elementos do response
		InputStreamResource resource = null;
		HttpStatus status = null;
		HttpHeaders header = new HttpHeaders();
		
		try {
			InputStream jsonInputStream = urlConnection();
		    JsonDataSource ds = new JsonDataSource(jsonInputStream, "F12014");   
			File arquivo = ResourceUtils.getFile("classpath:reports/ExF10214v2.jasper");
			JasperReport report = 
					(JasperReport) JRLoader.loadObjectFromFile(arquivo.getAbsolutePath());
			bytes = JasperRunManager.runReportToPdf(report, param, ds);
		} catch (IOException | JRException e) {
			e.printStackTrace();
			erro = e.getMessage();
			status = HttpStatus.BAD_REQUEST;
		} finally {
			if (erro.equals("")) {
				InputStream inputStream = new ByteArrayInputStream(bytes);
				resource = new InputStreamResource(inputStream);
				header.setContentLength(bytes.length);
				header.setContentType(MediaType.APPLICATION_PDF);
				status = HttpStatus.OK;
			}
		}
		
		return new ResponseEntity(resource, header, status);
	}

	private InputStream urlConnection() throws MalformedURLException, IOException {
		String sURL = "http://demo5624235.mockable.io/f12014"; 
 
		URL url = new URL(sURL);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(60 * 1000);
		connection.setReadTimeout(60 * 1000);
		InputStream jsonInputStream = connection.getInputStream();
		return jsonInputStream;
	}
}
