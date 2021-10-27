import { useEffect, useState } from "react";
import axios from "axios";
import { Button, TextField, Grid, Typography } from "@material-ui/core";
import {
  OutlinedInput,
  InputLabel,
  MenuItem,
  FormControl,
  Select,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import { apiUrl } from "../config.json";

export default function User({
  location: {
    state: { firstName, id },
  },
}) {
  const [projects, setProjects] = useState([{ id: 0 }]);
  const [selectedProject, setSelectedProject] = useState({});
  const [hasSessions, setHasSessions] = useState(false);
  const [input, setInput] = useState("");
  const [update, setUpdate] = useState(false);

  useEffect(() => {
    const getProjects = async () => {
      const { data } = await axios.get(`${apiUrl}/users/${id}/projects`);
      setProjects(data);
      setUpdate(false);
    };
    getProjects();
  }, [update, id]);

  const handleSelect = (event) => {
    const {
      target: { value },
    } = event;
    console.log(value);
    setSelectedProject(value);
  };

  const handleClose = () => {
    setHasSessions(false);
  };

  const handleDelete = async () => {
    console.log(selectedProject.id);
    // const sessions = {
    //   data: [
    //     {
    //       id: 1,
    //       startTime: "2019-02-18T20:00Z",
    //       endTime: "2019-02-18T20:00Z",
    //       counter: 1,
    //     },
    //   ],
    // };
    const sessions = await axios.get(
      `${apiUrl}/users/${id}/projects/${selectedProject.id}/sessions`
    );
    if (sessions.data.length === 0) {
      deleteProject();
    } else {
      setHasSessions(true);
    }
  };

  const deleteProject = async () => {
    // This is for test-case. Later delete console.log and uncomment axios part
    const res = await axios.delete(
      `${apiUrl}/users/${id}/projects/${selectedProject.id}`
    );
    if (res.status === 200) {
      alert('Project "' + res.data.projectname + '" is successfully deleted.');
    }
    setSelectedProject({});
    setHasSessions(false);
    setUpdate(true);
  };

  const handleInput = (event) => {
    const {
      target: { value },
    } = event;
    setInput(value);
  };

  const handleCreate = async () => {
    const res = await axios.post(`${apiUrl}/users/${id}/projects`, {
      projectname: input,
    });
    console.log(res.data.id);
    setInput("");
    setUpdate(true);
  };

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };

  return (
    <div style={{ margin: 20 }}>
      <Typography component="h6" align="right">{`Hi, ${firstName}`}</Typography>
      <Typography variant="inherit" component="h1" style={{marginTop: -20 }}>
        Manage Projects
      </Typography>
      <Grid style={gridStyle}>
        <FormControl sx={{ width: 200 }}>
          <InputLabel>Project</InputLabel>
          <Select
            value={selectedProject}
            onChange={handleSelect}
            input={<OutlinedInput label="Name" />}
          >
            {projects.map((project) => (
              <MenuItem key={project.id} value={project}>
                {project.projectname}
              </MenuItem>
            ))}
          </Select>
          <Button variant="contained" onClick={handleDelete}>
            Delete
          </Button>
        </FormControl>

        <FormControl style={{ marginLeft: 30 }}>
          <TextField
            id="outlined-basic"
            label="New Project"
            variant="outlined"
            onChange={handleInput}
            value={input}
          />
          <Button variant="contained" onClick={handleCreate}>
            Create
          </Button>
        </FormControl>

        <Dialog
          open={hasSessions}
          onClose={handleClose}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description"
        >
          <DialogTitle id="alert-dialog-title">
            Delete project "{selectedProject.projectname}"?
          </DialogTitle>
          <DialogContent>
            <DialogContentText id="alert-dialog-description">
              This project has an associated pomodoro(s). Deleting the project
              will result in losing all information about the pomodoro(s). Do
              you still want to delete this project?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={deleteProject}>Delete</Button>
            <Button onClick={handleClose} autoFocus>
              Cancel
            </Button>
          </DialogActions>
        </Dialog>
      </Grid>
    </div>
  );
}
