package org.cisiondata.modules.oauth.controller;

import java.security.Principal;
import java.util.List;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.oauth.entity.User;
import org.cisiondata.modules.oauth.service.IUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(value = "/oauth")
public class UserController {

	@Resource(name = "userService")
	private IUserService userService;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public Principal user(Principal user) {
		return user;
	}

	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole('admin')")
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@ResponseBody
	public List<User> listAllUsers() {
		List<User> users = (List<User>) userService.readDataListByCondition(new Query(), false);
		return users.isEmpty() ? null : users;
	}
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<User> getUser(@PathVariable("id") long id) {
		User user = (User) userService.readDataByPK(id, false);
		if (user == null) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('insert-user')")
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public ResponseEntity<Void> insertUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		if (null != userService.readUserByUsername(user.getUsername())) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
		userService.insert(user);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {
		User currentUser = (User) userService.readDataByPK(id, false);
		if (currentUser == null) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		currentUser.setUsername(user.getUsername());
		currentUser.setPassword(user.getPassword());
		userService.update(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	 * public ResponseEntity<User> deleteUser(@PathVariable("id") long id) {
	 * System.out.println("Fetching & Deleting User with id " + id);
	 * 
	 * User user = userService.findById(id); if (user == null) {
	 * System.out.println("Unable to delete. User with id " + id + " not found"
	 * ); return new ResponseEntity<User>(HttpStatus.NOT_FOUND); }
	 * 
	 * userService.deleteUserById(id); return new
	 * ResponseEntity<User>(HttpStatus.NO_CONTENT); }
	 */

	/*
	 * @RequestMapping(value = "/user/", method = RequestMethod.DELETE) public
	 * ResponseEntity<User> deleteAllUsers() { System.out.println(
	 * "Deleting All Users");
	 * 
	 * userService.deleteAllUsers(); return new
	 * ResponseEntity<User>(HttpStatus.NO_CONTENT); }
	 */
}
