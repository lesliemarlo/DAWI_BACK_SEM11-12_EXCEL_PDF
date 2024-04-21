package com.centroinformacion.service;

import java.util.List;

import com.centroinformacion.entity.Revista;

public interface RevistaService {
	
	//Para el crud
	public abstract Revista insertaActualizaRevista(Revista obj);
	public abstract List<Revista> listaRevistaPorNombreLike(String nombre);
	public abstract void eliminaRevista(int idRevista);
	public abstract List<Revista> listaTodos();
	
	//Validaciones Para Revistrar
	public abstract List<Revista> listaRevistaPorNombreIgualRegistra(String nombre);
	
	//Validaciones Para Actualizar
	public abstract List<Revista> listaRevistaPorNombreIgualActualiza(String nombre, int idRevista);
	
	
	
}
