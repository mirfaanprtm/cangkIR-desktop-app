package main.Repos;

import java.util.List;

import main.Models.Courier;

public interface ICourierRepo {
	List<Courier> getAll() throws Exception;
}
