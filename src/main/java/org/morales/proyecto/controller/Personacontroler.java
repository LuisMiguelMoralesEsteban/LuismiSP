package org.morales.proyecto.controller;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.morales.proyecto.domain.Pais;
import org.morales.proyecto.domain.Persona;
import org.morales.proyecto.domain.Venta;
import org.morales.proyecto.exception.DangerException;
import org.morales.proyecto.exception.InfoException;
import org.morales.proyecto.helper.H;
import org.morales.proyecto.helper.PRG;
import org.morales.proyecto.repository.Paisrepositorio;
import org.morales.proyecto.repository.Perosnarepositorio;
import org.morales.proyecto.repository.Ventarepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/persona")
public class Personacontroler {
	
	@Autowired
	private Perosnarepositorio repoPersona;
	
	@Autowired
	private Paisrepositorio repoPais;
	@Autowired
	private Ventarepositorio repoventa;
	@Value("${app.uploadFolder}")
	private String UPLOADED_FOLDER;

	@GetMapping("u")
	public String actualizarGet(ModelMap m, @RequestParam("id") Long id, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);

		m.put("paises", repoPais.findAll());

		m.put("persona", repoPersona.getOne(id));
		
		m.put("view", "/persona/personaU");
		return "/_t/frame";
	}
	@PostMapping("u")
	public void updatePost(
			@RequestParam("id") Long id,
			@RequestParam("nombre") String nombre,
			@RequestParam("loginname") String loginname, 
			@RequestParam("password") String password,
			@RequestParam(value="altura") Integer altura, 
			@RequestParam(value="fnac")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate fnac,
			@RequestParam(value = "idPais", required = false ) Long idPais,
			@RequestParam("img") MultipartFile imgFile
			
			) throws DangerException, InfoException {
		try {
			Persona persona = repoPersona.getOne(id);

		
			persona.setNombre(nombre);
			persona.setLoginname(loginname);
			persona.setAltura(altura);
			persona.setFnac(fnac);
			Pais paisNacimiento = repoPais.getOne(idPais);
			if(idPais!=0) {
			paisNacimiento.getNace().add(persona);
			
			
			persona.setNace(paisNacimiento);}
			else {
				persona.setNace(null);
				
			}
			String uploadDir = "/img/upload/";
			String uploadDirRealPath = "";
			String fileName = "defeault";
			

			if (imgFile != null && imgFile.getOriginalFilename().split("\\.").length == 2) {
				fileName = "persona-" + persona.getLoginname();
			
				uploadDirRealPath = UPLOADED_FOLDER;
				
				File transferFile = new File(uploadDirRealPath + fileName + "." + "png");
				imgFile.transferTo(transferFile);
			}

		String img ="png";
		
		String sFichero = uploadDirRealPath + fileName + "." + "png";
		File fichero = new File(sFichero);

		if (fichero.exists())
			 persona.setImg(img );
		else {
			persona.setImg(null);
		}
		   

			 
		
		
			
			
			
			repoPersona.save(persona);
			
			
			
					
						
						
						
			
		}
		
		catch (Exception e) {
			PRG.error("Error al actualizar " + nombre + " // "+e.getMessage(), "/persona/r");
		}

		PRG.info("Persona " + nombre + " actualizada correctamente", "/persona/r");
		
	
		
		}
		
	

	@PostMapping("d")
	public String borrarPost(@RequestParam("id") Long idPersona, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		String nombrePersona = "----";
		try {
			Persona persona = repoPersona.getOne(idPersona);
			nombrePersona = persona.getNombre();
			repoPersona.delete(persona);
		} catch (Exception e) {
			PRG.error("Error al borrar la persona " + nombrePersona, "/persona/r");
		}
		return "redirect:/persona/r";
	}
	
	
	
	
	@GetMapping("r")
	public String mostrar(ModelMap m) {
		m.put("personas", repoPersona.findAll());

		m.put("view", "/persona/personaR");
		return "/_t/frame";
	}
	
	
	@GetMapping("c")
	public String registro(ModelMap m) {
		m.put("paises", repoPais.findAll());
		m.put("view", "/persona/personaC");
		
		return "/_t/frame";
	}
	
	@PostMapping("c")
	public String crearPost(
			@RequestParam("nombre") String nombre,
			@RequestParam("loginname") String loginname, 
			@RequestParam("password") String password,
			@RequestParam(value="altura") Integer altura, 
			@RequestParam(value="fnac")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate fnac,
			@RequestParam(value = "idPais", required = false ) Long idPais,
			@RequestParam("img") MultipartFile imgFile
			
			) throws DangerException, InfoException {
		try {	BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
	
		Persona persona = new Persona(nombre, loginname, bpe.encode(password), altura, fnac );
		
		LocalDate ahora =  LocalDate.now().plusDays(1);
		Venta Venta = new Venta( ahora, null);
		
		Pais paisNacimiento = repoPais.getOne(idPais);
		
		if(idPais!=0) {
		paisNacimiento.getNace().add(persona);
		
		persona.setNace(paisNacimiento);}
		
		String uploadDir = "/img/upload/";
		String uploadDirRealPath = "";
		String fileName = "defeault";
		

		if (imgFile != null && imgFile.getOriginalFilename().split("\\.").length == 2) {
			fileName = "persona-" + persona.getLoginname();
		
			uploadDirRealPath = UPLOADED_FOLDER;
			
			File transferFile = new File(uploadDirRealPath + fileName + "." + "png");
			imgFile.transferTo(transferFile);
		}
	String img ="png";
		
		String sFichero = uploadDirRealPath + fileName + "." + "png";
		File fichero = new File(sFichero);

		if (fichero.exists())
			 persona.setImg(img );
		else {
			persona.setImg(null);
		}
		
		repoPersona.save(persona);
		
		repoventa.save(Venta);
		Venta.setVentaencurso(persona);	
		repoventa.save(Venta);
	
	
		}
		catch (Exception e) {
			
			PRG.error("error al crear " + nombre, "/persona/r");
			}
		
		PRG.info("usuario creado correctamente");
		return "";}
		
	}
	
