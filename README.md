This is a ignite sample datagrid project with third party persistence. Project settings made according to local postgresql installation. You can find table creation script in ```create_table_userinfo.txt``` file. After copying all necessary jars into lib directory start server nodes using `run_server.sh` . You can start grid data loadaer client with
```
run_loader.sh -l
```
command.

###Generating Records
You can generate records using ```info.serdroid.userinfo.grid.UserInfoGenerator``` class. This class generates '#' seperated records into ```userinfo.txt``` file. You can import this records into database with psql client using below command.
```
\copy userinfo from '/path/to/project/userinfo.txt' with delimiter '#"
```