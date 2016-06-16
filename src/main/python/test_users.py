import os, sys, time, re
import inspect
import requests, json
import random

requests.packages.urllib3.disable_warnings()

#curl -X GET --header "Accept: application/json" --header "user: -1" --header "page: 0" --header "size: 0" --header "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6IjEiLCJyb2xlIjoiUk9MRV9BRE1JTiJ9.Wxzg8LVcHoNlkz_nKkt1cbs-aoKmyIPgcM5hnNX6vUhDQAl1_Hwz-wXjJZuNkJtk4uijgiyf6v-MQFNBrJXfBA" "https://localhost:8443/api/entries"

baseuri = "https://localhost:8443/"

# login as a specific user and retrieve auth token
def login(email, password):
	endpoint = baseuri+"auth"
	params = {"email":email, "password":password}
	r = requests.post(endpoint, verify=False, headers=params)
	return json.loads(r.text)

def get_users_by_email(token, role):
	count = 0

	endpoint = baseuri+"api/users?field=email"

	params = {"page":"0", "size":"0", "Authorization":"Bearer "+token}
	r = requests.get(endpoint, verify=False, headers=params)
	assert(r.status_code == requests.codes.ok or  role == 'ROLE_USER')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of all users ", len(users)
		count = count + len(users)

	params = {"page":"0", "size":"0", "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=user@test.com", verify=False, headers=params)
	assert(r.status_code == requests.codes.ok  or  role == 'ROLE_USER')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of users ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=manager@test.com", verify=False, headers=params)
	assert(r.status_code == requests.codes.ok or  role != 'ROLE_ADMIN')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of managers ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=admin@test.com", verify=False, headers=params)
	assert(r.status_code == requests.codes.ok or  role != 'ROLE_ADMIN')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of admins ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=noname@test.com", verify=False, headers=params)
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of noname users ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=", verify=False, headers=params)
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of empty names ", len(users)
		count = count + len(users)

	return count

def get_users_in_role(token, role):
	count = 0

	endpoint = baseuri+"api/users?field=role"

	params = {"page":"0", "size":"0", "Authorization":"Bearer "+token}
	r = requests.get(endpoint, verify=False, headers=params)
	assert(r.status_code == requests.codes.ok or  role == 'ROLE_USER')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of all users ", len(users)
		count = count + len(users)

	params = {"page":"0", "size":"0", "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=ROLE_USER", verify=False, headers=params)
	assert(r.status_code == requests.codes.ok  or  role == 'ROLE_USER')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)
		print "Number of users ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=ROLE_MANAGER", verify=False, headers=params)
	assert(r.status_code == requests.codes.ok or  role != 'ROLE_ADMIN')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)[0]
		print "Number of managers ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=ROLE_ADMIN", verify=False, headers=params)
	assert(r.status_code == requests.codes.ok or  role != 'ROLE_ADMIN')
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)[0]
		print "Number of admins ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=ROLE_JUNK", verify=False, headers=params)
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)[0]
		print "Number of admins ", len(users)
		count = count + len(users)

	params = {"page":0, "size":0, "Authorization":"Bearer "+token}
	r = requests.get(endpoint+"&value=", verify=False, headers=params)
	if r.status_code == requests.codes.ok:
		users = json.loads(r.text)[0]
		print "Number of admins ", len(users)
		count = count + len(users)

	return count


def test_user_get():
	print "*********......CHECK BY ROLE.......*******"
	# Use this to insert and test users
	print "Getting for ADMIN"
	admin = login("admin@test.com", "admin")
	token = admin[u'token']
	role =  admin[u'user'][u'role'][u'id']
	admincount = get_users_in_role(token, role)

	print "Getting for USER"
	user = login("user@test.com", "user")
	token = user[u'token']
	role =  user[u'user'][u'role'][u'id']
	usercount = get_users_in_role(token, role)

	assert (usercount == 0)

	print "Getting for MANAGER"
	mgr = login("manager@test.com", "mgr")
	token = mgr[u'token']
	role =  mgr[u'user'][u'role'][u'id']
	mgrcount = get_users_in_role(token, role)
	assert (mgr > 0)

	print "*********......CHECK BY EMAIL.......*******"
	print "Getting for ADMIN"
	admin = login("admin@test.com", "admin")
	token = admin[u'token']
	role =  admin[u'user'][u'role'][u'id']
	admincount = get_users_by_email(token, role)

	print "Getting for USER"
	user = login("user@test.com", "user")
	token = user[u'token']
	role =  user[u'user'][u'role'][u'id']
	usercount = get_users_by_email(token, role)

	assert (usercount == 0)

	print "Getting for MANAGER"
	mgr = login("manager@test.com", "mgr")
	token = mgr[u'token']
	role =  mgr[u'user'][u'role'][u'id']
	mgrcount = get_users_by_email(token, role)
	assert (mgr > 0)

test_user_get()


def userinfo(i, prefix, role):
	user = {"email": "%s%s@test.com" % (prefix, i),
				"name": "%s%s" % (prefix, i),
				"password": "%s%s" % (prefix, i),
				"role":role}
	return user


def add_delete_users(users, role, token):
	print "*********......ADD/DELETE Users.......*******"
	endpoint = baseuri+"api/users"
	for u in users:
		print "Try to add/delete ", u['name'],
		try:
			params = u
			params["Authorization"] = "Bearer " + token
			r = requests.post(endpoint, verify=False, headers=params)
			assert(r.status_code == requests.codes.created or role == 'ROLE_USER' or
				    (role == 'ROLE_MANAGER' and u['role'] != 'ROLE_USER'))
			if (r.status_code != requests.codes.created):
				print " failed "
				continue
			# Else delete the user
			endpoint = baseuri+"api/users?field=email"
			params = {"page":"0", "size":"0", "Authorization":"Bearer "+token}
			r = requests.get(endpoint+"&value=%s" % u['email'], verify=False, headers=params)
			id = json.loads(r.text)[0][u'id']
			r = requests.delete(baseuri+"api/users/"+str(id), verify=False, headers={"Authorization":"Bearer "+token})
			assert(r.status_code == requests.codes.no_content)
			# Try deleting them again
			r = requests.delete(baseuri+"api/users/"+str(id), verify=False, headers={"Authorization":"Bearer "+token})
			assert(r.status_code == requests.codes.not_found)
			print " succeeded "
		except:
			pass

def test_user_add_delete():
	users = []
	for i in range(10, 15):
		users.append(userinfo(i, "user", "ROLE_USER"))
	for i in range(30, 35):
		users.append(userinfo(i, "mgr", "ROLE_MANAGER"))
	for i in range(20, 25):
		users.append(userinfo(i, "admin", "ROLE_ADMIN"))

	print "*********......CREATE/DELETE BY ROLE.......*******"
	# Use this to insert and test users
	print "Running as ADMIN"
	admin = login("admin@test.com", "admin")
	token = admin[u'token']
	role =  admin[u'user'][u'role'][u'id']
	add_delete_users(users, role, token)

	print "Running as USER"
	user = login("user@test.com", "user")
	token = user[u'token']
	role =  user[u'user'][u'role'][u'id']
	add_delete_users(users, role, token)


	print "Running as MANAGER"
	mgr = login("manager@test.com", "mgr")
	token = mgr[u'token']
	role =  mgr[u'user'][u'role'][u'id']
	add_delete_users(users, role, token)

test_user_add_delete()