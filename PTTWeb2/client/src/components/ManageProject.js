import { useState } from "react";
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
import axios from "axios";
import { apiUrl } from "../config.json";

export default function ManageProject({ id, projects, setUpdate }) {
  const [input, setInput] = useState("");
  const [hasSessions, setHasSessions] = useState(false);
  const [selectedProject, setSelectedProject] = useState({});

  const deleteProject = async () => {
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
    try {
      const res = await axios.post(`${apiUrl}/users/${id}/projects`, {
        projectname: input,
      });
      console.log(res.data.id);
      setInput("");
      setUpdate(true);
    } catch (e) {
      if (e.response.status === 409) {
        alert(`Project ${input} already exists!`);
      }
    }
  };

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
    const sessions = await axios.get(
      `${apiUrl}/users/${id}/projects/${selectedProject.id}/sessions`
    );
    if (sessions.data.length === 0) {
      deleteProject();
    } else {
      setHasSessions(true);
    }
  };

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };
  return (
    <div style={{ margin: 30 }}>
      <Typography variant="inherit" component="h1" style={{ padding: 0 }}>
        Manage Projects
      </Typography>
      <Grid style={gridStyle}>
        <FormControl sx={{ width: 200 }}>
          <InputLabel>Project</InputLabel>
          <Select
            id="existing-projects-select"
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
          <Button
            id="delete-project-btn"
            variant="outlined"
            onClick={handleDelete}
          >
            Delete
          </Button>
        </FormControl>

        <FormControl style={{ marginLeft: 30 }}>
          <TextField
            id="project-input"
            label="New Project"
            variant="outlined"
            onChange={handleInput}
            value={input}
          />
          <Button
            id="project-create-btn"
            variant="outlined"
            onClick={handleCreate}
          >
            Create
          </Button>
        </FormControl>
      </Grid>
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
            will result in losing all information about the pomodoro(s). Do you
            still want to delete this project?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button id="dialog-accept" onClick={deleteProject}>
            Delete
          </Button>
          <Button id="dialog-cancel" onClick={handleClose} autoFocus>
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
