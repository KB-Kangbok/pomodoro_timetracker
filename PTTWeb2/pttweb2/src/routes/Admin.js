import { useEffect, useState } from "react";
import axios from "axios";
import { Button, TextField, Grid } from "@material-ui/core";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import { apiUrl } from "../config.json";

function Admin() {
  const [users, setUsers] = useState([{ id: 0 }]);
  const [inputFnameNew, setInputFnameNew] = useState("");
  const [inputLnameNew, setInputLnameNew] = useState("");
  const [inputEmailNew, setInputEmailNew] = useState("");

  const [inputFnameEdit, setInputFnameEdit] = useState("");
  const [inputLnameEdit, setInputLnameEdit] = useState("");
  const [selectedEditUser, setSelectedEditUser] = useState({});

  const [selectedDelUser, setSelectedDelUser] = useState({});
  const [update, setUpdate] = useState(false);

  useEffect(() => {
    const getAllUsers = async () => {
      const { data } = await axios.get(`${apiUrl}/users`);
      setUsers(data);
      setUpdate(false);
    };
    getAllUsers();
  }, [update]);

  const handleInputFnameNew = (event) => {
    const {
      target: { value },
    } = event;
    setInputFnameNew(value);
  };

  const handleInputLnameNew = (event) => {
    const {
      target: { value },
    } = event;
    setInputLnameNew(value);
  };

  const handleInputEmailNew = (event) => {
    const {
      target: { value },
    } = event;
    setInputEmailNew(value);
  };

  const handleCreate = async () => {
    await axios.post(`${apiUrl}/users`, {
      firstName: inputFnameNew,
      lastName: inputLnameNew,
      email: inputEmailNew,
    });
    setInputFnameNew("");
    setInputLnameNew("");
    setInputEmailNew("");
    setUpdate(true);
  };

  const handleEditSelect = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedEditUser(value);
    setInputFnameEdit(value.firstName);
    setInputLnameEdit(value.lastName);
  };

  const handleInputFnameEdit = (event) => {
    const {
      target: { value },
    } = event;
    setInputFnameEdit(value);
  };

  const handleInputLnameEdit = (event) => {
    const {
      target: { value },
    } = event;
    setInputLnameEdit(value);
  };

  const handleUpdate = async () => {
    await axios.put(`${apiUrl}/users/${selectedEditUser.id}`, {
      firstName: inputFnameEdit,
      lastName: inputLnameEdit,
      email: selectedEditUser.email,
    });
    setInputFnameEdit("");
    setInputLnameEdit("");
    setUpdate(true);
  };

  const handleDelSelect = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedDelUser(value);
  };

  const handleDelete = async () => {
    //check if the selected user has project or not
    const projects = await axios.get(
      `${apiUrl}/users/${selectedDelUser.id}/projects`
    );
    let deleteSign = true;
    if (projects.data.length > 0) {
      if (!window.confirm("Are you sure? The user has project(s)!")) {
        deleteSign = false;
      }
    }
    if (deleteSign) {
      await axios.delete(`${apiUrl}/users/${selectedDelUser.id}`);
      setSelectedDelUser({});
      setUpdate(true);
    }
  };
  return (
    <div style={{ margin: 20 }}>
      <h1>Admin - Manage Users</h1>
      <Grid container direction="row" justifyContent="space-around">
        <Grid item>
          <h2>Add User</h2>
          <FormControl sx={{ width: 200 }}>
            <TextField
              required
              id="outlined-basic"
              label="First Name"
              variant="outlined"
              onChange={handleInputFnameNew}
              value={inputFnameNew}
              margin="dense"
            />
            <TextField
              required
              id="outlined-basic"
              label="Last Name"
              variant="outlined"
              onChange={handleInputLnameNew}
              value={inputLnameNew}
              margin="dense"
            />
            <TextField
              required
              id="outlined-basic"
              label="Email"
              variant="outlined"
              onChange={handleInputEmailNew}
              value={inputEmailNew}
              margin="dense"
            />
            <Button variant="contained" onClick={handleCreate}>
              Create User
            </Button>
          </FormControl>
        </Grid>

        <Grid item>
          <h2>Edit User</h2>
          <FormControl sx={{ m: 1, width: 200 }}>
            <InputLabel>User</InputLabel>
            <Select
              value={selectedEditUser}
              onChange={handleEditSelect}
              input={<OutlinedInput label="Name" />}
              margin="dense"
            >
              {users.map((user) => (
                <MenuItem key={user.id} value={user}>
                  {user.email}
                </MenuItem>
              ))}
            </Select>
            <TextField
              id="outlined-basic"
              label="First Name"
              variant="outlined"
              onChange={handleInputFnameEdit}
              value={inputFnameEdit}
              margin="dense"
            />
            <TextField
              id="outlined-basic"
              label="Last Name"
              variant="outlined"
              onChange={handleInputLnameEdit}
              value={inputLnameEdit}
              margin="dense"
            />
            <Button variant="contained" onClick={handleUpdate}>
              Update User
            </Button>
          </FormControl>
        </Grid>

        <Grid item>
          <h2>Delete User</h2>
          <FormControl sx={{ m: 1, width: 200 }}>
            <InputLabel>User</InputLabel>
            <Select
              value={selectedDelUser}
              onChange={handleDelSelect}
              input={<OutlinedInput label="Name" />}
            >
              {users.map((user) => (
                <MenuItem key={user.id} value={user}>
                  {user.email}
                </MenuItem>
              ))}
            </Select>
            <Button variant="contained" onClick={handleDelete}>
              Delete User
            </Button>
          </FormControl>
        </Grid>
      </Grid>
    </div>
  );
}

export default Admin;
