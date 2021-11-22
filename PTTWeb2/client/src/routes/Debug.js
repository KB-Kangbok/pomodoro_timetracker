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
import Session from "../components/Session";
import Timer from "../components/Timer";

export default function Debug({
  location: {
    state: { firstName, id, startSession },
  },
}) {
  const [projects, setProjects] = useState([{ id: 0 }]);
  const [selectedProject, setSelectedProject] = useState({});
  const [hasSessions, setHasSessions] = useState(false);
  const [input, setInput] = useState("");
  const [update, setUpdate] = useState(false);
  const [startedSession, setStartedSession] = useState(false);
  const [clickcStartSession, setClickcStartSession] = useState(false);
  const [countdown, setCountdown] = useState(300);

  useEffect(() => {
    const getProjects = async () => {
      const { data } = await axios.get(`${apiUrl}/users/${id}/projects`);
      setProjects(data);
      setUpdate(false);
    };
    getProjects();
  }, [update]);

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

  const handleClose2 = () => {
    setClickcStartSession(false);
  };

  const handleClickStartSession = () => {
    setClickcStartSession(true);
    if (selectedProject.id != null) {
      setStartedSession(true);
    }
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

  const handleSession = async () => {
    setStartedSession(true);
    setClickcStartSession(false);
    // try {
    //   //delete later - handled in Session.js
    //   const res = await axios.post(
    //     `${apiUrl}/users/${id}/projects/${selectedProject.id}/sessions`,
    //     {
    //       startTime: "2019-02-18T20:00Z",
    //       endTime: "2019-02-18T21:00Z",
    //       counter: 0,
    //     }
    //   );
    //   console.log(res.data.id);
    //   // setSelectedProject({});
    //   setUpdate(true);
    // } catch (e) {
    //   if (e.response.status === 404) {
    //     alert(`Bad request`);
    //   }
    // }
  };

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };
  return (
    <div
      className="font-sans"
      style={{ margin: 30, marginTop: 0, color: "#414244" }}
    >
      <Typography
        id="greeting"
        component="h6"
        align="right"
      >{`Hi, ${firstName}`}</Typography>
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
            id="create-session-btn"
            variant="outlined"
            onClick={handleClickStartSession}
          >
            Start a session
          </Button>
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
        <Timer countdown={countdown} setCountdown={setCountdown} />
        <div style={{ position: "absolute", paddingLeft: 20 }}>
          {startedSession && <Session user={id} project={selectedProject.id} />}
        </div>
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
      <Dialog
        open={selectedProject.id == null && clickcStartSession}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-session-no-project">
          Start a session without a project?
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-session-no-project-msg">
            No project is chosen to be associated. Do you want to continue
            starting a new session without a project?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button id="dialog-accept-2" onClick={handleSession}>
            Ok
          </Button>
          <Button id="dialog-cancel-2" onClick={handleClose2} autoFocus>
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
