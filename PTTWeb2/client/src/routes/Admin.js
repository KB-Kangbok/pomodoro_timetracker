import { useEffect, useState } from "react";
import axios from "axios";
import { Typography, Button, TextField, Grid } from "@material-ui/core";
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
    if (inputFnameNew === "" || inputLnameNew === "" || inputEmailNew === "") {
      alert("Please fill in all the fields!");
      return;
    }
    try {
      const res = await axios.post(`${apiUrl}/users`, {
        firstName: inputFnameNew,
        lastName: inputLnameNew,
        email: inputEmailNew,
      });
      if (res.status === 201) {
        alert('User "' + res.data.email + '" is successfully created.');
        setInputFnameNew("");
        setInputLnameNew("");
        setInputEmailNew("");
      }
    } catch (e) {
        if (e.response.status === 409) {
          alert(`User with email ${inputEmailNew} already exists!`);
        } else {
          alert(`Create user failed with ${e.status} code`);
        }
    }
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
    if (inputFnameEdit === "" || inputLnameEdit === "") {
      alert("Firstname or lastname cannot be empty!");
      return;
    }
    try {
      const res = await axios.put(`${apiUrl}/users/${selectedEditUser.id}`, {
        firstName: inputFnameEdit,
        lastName: inputLnameEdit,
        email: selectedEditUser.email,
      });
      if (res.status === 200) {
        alert('User "' + res.data.email + '" is successfully edited.');
        setInputFnameEdit("");
        setInputLnameEdit("");
      }
    } catch (e) {
      alert(`Edit user failed with ${e.response.status} code`);
    }
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
      <Typography component="h6" align="right">
        Hi, Admin
      </Typography>
      <Typography variant="inherit" component="h1" style={{ marginTop: -20 }}>
        Manage Users
      </Typography>

      <Grid container direction="row" justifyContent="center">
        <Grid item style={{ marginRight: 50 }}>
          <h2>Add User</h2>
          <FormControl sx={{ width: 200 }}>
            <TextField
              required
              id="fname-input"
              label="First Name"
              variant="outlined"
              onChange={handleInputFnameNew}
              value={inputFnameNew}
              margin="dense"
            />
            <TextField
              required
              id="lname-input"
              label="Last Name"
              variant="outlined"
              onChange={handleInputLnameNew}
              value={inputLnameNew}
              margin="dense"
            />
            <TextField
              required
              id="email-input"
              label="Email"
              variant="outlined"
              onChange={handleInputEmailNew}
              value={inputEmailNew}
              margin="dense"
            />
            <Button id="create-user-btn" variant="contained" onClick={handleCreate}>
              Create
            </Button>
          </FormControl>
        </Grid>

        <Grid item style={{ marginRight: 50 }}>
          <h2>Edit User</h2>
          <FormControl sx={{ width: 200 }}>
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
              Update
            </Button>
          </FormControl>
        </Grid>

        <Grid item>
          <h2>Delete User</h2>
          <FormControl  sx={{ width: 200 }}>
            <InputLabel>User</InputLabel>
            <Select
                id="delete-email-select"
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
            <Button id="delete-user-btn" variant="contained" onClick={handleDelete}>
              Delete
            </Button>
          </FormControl>
        </Grid>
      </Grid>
    </div>
  );
}

export default Admin;
