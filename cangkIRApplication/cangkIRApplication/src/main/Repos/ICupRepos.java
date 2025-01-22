package main.Repos;

import java.util.List;

import main.Models.Cup;

public interface ICupRepos {
	Cup create(Cup cup) throws Exception;
	List<Cup> getAll() throws Exception;
	void delete(String cupName) throws Exception;
	Cup update(Cup cup) throws Exception;
	List<Cup> findByName(String cupName) throws Exception;
	Cup findById(String cupId) throws Exception;
}
