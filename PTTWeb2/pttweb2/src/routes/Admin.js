import { useEffect, useState } from "react";
import axios from "axios";
import { Button, TextField, Grid } from "@material-ui/core";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import { apiUrl } from "../config.json";

const handleCreate = async () => {

};

const handleInput = (event) => {

};

const handleSelect = (event) => {

};

const handleUpdate = (event) => {

};

const handleDelete = async () => {

};

//needs to be filled out
// const [selectedUser, setSelectedUser] = 

function Admin() {
	return (
		<div style={{margin: 20}}>
			<h1>Admin - Manage Users</h1>
			<h2>Add User</h2>
			<FormControl sx={{ m: 1, width: 200 }}>
				<TextField
					required id="outlined-basic"
					label="First Name"
					variant="outlined"
					// onChange={handleInput}
					// value={input}
				/>
				<TextField
					required id="outlined-basic"
					label="Last Name"
					variant="outlined"
					// onChange={handleInput}
					// value={input}
				/>
				<TextField
					required id="outlined-basic"
					label="Email"
					variant="outlined"
					// onChange={handleInput}
					// value={input}
				/>
				<Button variant="contained" onClick={handleCreate}>Create User</Button>
			</FormControl>
			<h2>Edit User</h2>
			<FormControl sx={{ m: 1, width: 200 }}>
				<InputLabel>User</InputLabel>
				<Select onChange={handleSelect} input={<OutlinedInput label="Name"/>}>
					{/* add value={selectedUser}  on line 62 before onChange*/}
					{/* need event handling */}
				</Select>
				<TextField
					id="outlined-basic"
					label="First Name"
					variant="outlined"
					// onChange={handleInput}
					// value={input}
				/>
				<TextField
					id="outlined-basic"
					label="Last Name"
					variant="outlined"
					// onChange={handleInput}
					// value={input}
				/>
				<Button variant="contained" onClick={handleUpdate}>Update User</Button>
			</FormControl>
			<h2>Delete User</h2>
			<FormControl sx={{ m: 1, width: 200 }}>
				<InputLabel>User</InputLabel>
				<Select onChange={handleSelect} input={<OutlinedInput label="Name"/>}>
					{/* add value={selectedUser}  on line 62 before onChange*/}
					{/* need event handling */}
				</Select>
				<Button variant="contained" onClick={handleDelete}>Delete User</Button>
			</FormControl>
		</div>
	)
}

export default Admin;
