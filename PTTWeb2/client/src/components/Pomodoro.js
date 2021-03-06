import { useState } from "react";
import {
  FormControl,
  Select,
  MenuItem,
  InputLabel,
  Grid,
  OutlinedInput,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from "@mui/material";
import Timer from "./Timer";
import axios from "axios";
import { apiUrl } from "../config.json";

export default function Pomodoro({ id, projects, isTest }) {
  const [selectedProjectId, setSelectedProjectId] = useState("");
  const [countdown, setCountdown] = useState(10);
  const [isRest, setIsRest] = useState(false);
  const [isTimer, setIsTimer] = useState(false);
  const [counter, setCounter] = useState(0);
  const [sessionId, setSessionId] = useState(0);
  const [stopDialog, setStopDialog] = useState(false);
  const [continueDialog, setContinueDialog] = useState(false);
  const [projDialog, setProjDialog] = useState(false);
  const [isAssociate, setIsAssociate] = useState(false);
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };

  const getTime = (time) => {
    const date = new Date(time);
    const year = date.getFullYear();
    const month = "0" + (date.getMonth() + 1);
    const day = "0" + date.getDate();
    // Hours part from the timestamp
    const hours = "0" + date.getHours();
    // Minutes part from the timestamp;
    const minutes = "0" + date.getMinutes();
    // Will display time in 2019-02-19T10:30Z format
    return (
      year +
      "-" +
      month.substr(-2) +
      "-" +
      day.substr(-2) +
      "T" +
      hours.substr(-2) +
      ":" +
      minutes.substr(-2) +
      "Z"
    );
  };
  const reset = () => {
    setIsAssociate(false);
    setStopDialog(false);
    setProjDialog(false);
    setContinueDialog(false);
  };

  const startTimer = () => {
    if (isTest) {
      // 2 seconds for test
      setCountdown(2);
    } else {
      // 25 minutes for actual
      setCountdown(1500);
    }
    setIsRest(false);
    setIsTimer(true);
  };

  const handlePomodoro = () => {
    setProjDialog(true);
  };

  const handleAssociateAccept = () => {
    setIsAssociate(true);
  };

  const handleAssociateDismiss = () => {
    setSelectedProjectId("");
    reset();
    startTimer();
  };

  const handleStart = () => {
    reset();
    startTimer();
    setSessionId("");
    if (!isTest || startTime === "") {
      setStartTime(getTime(Date.now()));
    }
  };

  const handleStop = () => {
    setIsTimer(false);
    setIsRest(false);
    if (selectedProjectId !== "") {
      if (isTest) {
        if (endTime === "") {
          // start + 1 minute for test
          const start = new Date(startTime.substr(0, startTime.length - 1));
          setEndTime(getTime(start.getTime() + 1 * 60 * 1000));
        } else {
          // add 30 minutes from previous endtime for test
          const prevEndTime = new Date(endTime.substr(0, endTime.length - 1));
          setEndTime(getTime(prevEndTime.getTime() + 31 * 60 * 1000));
        }
      } else {
        // now for actual
        setEndTime(getTime(Date.now()));
      }
      setStopDialog(true);
    }
  };

  const handleSelect = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedProjectId(value);
  };

  const handleStopDialogAccept = async () => {
    const request = {
      startTime: startTime,
      endTime: endTime,
      counter: counter,
    };
    const url = `${apiUrl}/users/${id}/projects/${selectedProjectId}/sessions`;
    if (sessionId !== "") {
      try {
        const res = await axios.put(`${url}/${sessionId}`, request);
        console.log(res);
      } catch (e) {
        if (e.response.status === 400) {
          alert("Bad request");
        } else if (e.response.status === 404) {
          alert("User, project, or session not found");
        } else {
          alert(`Failed with ${e.response.status} status`);
        }
      }
    } else {
      try {
        const res = await axios.post(url, request);
        console.log(res.data.id);
      } catch (e) {
        if (e.response.status === 400) {
          alert("Bad request");
        } else if (e.response.status === 404) {
          alert("User, project, or session not found");
        } else {
          alert(`Failed with ${e.response.status} status`);
        }
      }
    }
    setSessionId("");
    setSelectedProjectId("");
    if (isTest) {
      setStartTime(endTime);
    } else {
      setStartTime("");
    }
    setEndTime("");
    setCounter(0);
    reset();
  };
  const handleStopDialogDismiss = () => {
    setSelectedProjectId("");
    setSessionId("");
    if (!isTest) {
      setStartTime("");
    }
    setEndTime("");
    setCounter(0);
    reset();
  };
  const handleContinueDialogAccept = async () => {
    const request = {
      startTime: startTime,
      endTime: endTime,
      counter: counter,
    };
    console.log(request);
    const url = `${apiUrl}/users/${id}/projects/${selectedProjectId}/sessions`;
    if (sessionId !== "") {
      try {
        const res = await axios.put(`${url}/${sessionId}`, request);
        console.log(res.data.id);
      } catch (e) {
        if (e.response.status === 400) {
          alert("Bad request");
        } else if (e.response.status === 404) {
          alert("User, project, or session not found");
        } else {
          alert(`Failed with ${e.response.status} status`);
        }
      }
    } else {
      try {
        const res = await axios.post(url, request);
        console.log(res.data.id);
        setSessionId(res.data.id);
      } catch (e) {
        if (e.response.status === 400) {
          alert("Bad request");
        } else if (e.response.status === 404) {
          alert("User, project, or session not found");
        } else {
          alert(`Failed with ${e.response.status} status`);
        }
      }
    }
    reset();
    startTimer();
  };

  const handleContinueDialogDismiss = async () => {
    const request = {
      startTime: startTime,
      endTime: endTime,
      counter: counter,
    };
    console.log(request);
    const url = `${apiUrl}/users/${id}/projects/${selectedProjectId}/sessions`;
    if (sessionId !== "") {
      try {
        const res = await axios.put(`${url}/${sessionId}`, request);
        console.log(res.data.id);
      } catch (e) {
        if (e.response.status === 400) {
          alert("Bad request");
        } else if (e.response.status === 404) {
          alert("User, project, or session not found");
        } else {
          alert(`Failed with ${e.response.status} status`);
        }
      }
    } else {
      try {
        console.log(`${getTime(startTime)} ${getTime(endTime)} ${counter}`);
        const res = await axios.post(url, request);
        console.log(res.data.id);
      } catch (e) {
        if (e.response.status === 400) {
          alert("Bad request");
        } else if (e.response.status === 404) {
          alert("User, project, or session not found");
        } else {
          alert(`Failed with ${e.response.status} status`);
        }
      }
    }
    setSelectedProjectId("");
    setSessionId("");
    if (isTest) {
      setStartTime(endTime);
    } else {
      setStartTime("");
    }
    setEndTime("");
    setCounter(0);
    reset();
  };

  return (
    <div style={{ margin: 30 }}>
      <Typography variant="inherit" component="h1" style={{ padding: 0 }}>
        Pomodoro
      </Typography>
      <Grid style={gridStyle}>
        <Grid
          container
          style={{ justifyContent: "center", alignContent: "center" }}
        >
          {!isTimer && (
            <Button
              id="start-pomodoro-btn"
              onClick={handlePomodoro}
              variant="outlined"
            >
              Start Pomodoro
            </Button>
          )}
          {isTimer && (
            <div>
              <Timer
                countdown={countdown}
                setCountdown={setCountdown}
                isRest={isRest}
                setIsRest={setIsRest}
                setIsTimer={setIsTimer}
                setContinueDialog={setContinueDialog}
                projectId={selectedProjectId}
                endTime={endTime}
                setEndTime={setEndTime}
                counter={counter}
                setCounter={setCounter}
                isTest={isTest}
                startTime={startTime}
              />
              <div style={{top: "-11vh", left: "14vh",position:"relative"}}>
              <Button id="stop-btn" onClick={handleStop}>
                stop
              </Button>
              </div>
            </div>
          )}
        </Grid>
        <Grid
          container
          item
          style={{
            // marginTop: 20,
            padding: 0,
            height: 60,
            alignItems: "center",
            justifyContent: "space-evenly",
          }}
        ></Grid>
      </Grid>
      <Dialog open={projDialog}>
        <DialogTitle>Start pomodoro</DialogTitle>
        <DialogContent id="associate-dlg">
          {!isAssociate && (
            <DialogContentText>
              Do you want to associate Pomodoro with project?
            </DialogContentText>
          )}
          {isAssociate && projects.length === 0 && (
            <div>
              <DialogContentText>
                You don't have any projects to associate!
              </DialogContentText>
              <DialogActions>
                <Button id="ok-btn" onClick={handleAssociateDismiss}>Ok</Button>
              </DialogActions>
            </div>
          )}
          {isAssociate && projects.length > 0 && (
            <FormControl sx={{ width: 200 }}>
              <InputLabel>Project</InputLabel>
              <Select
                id="projList"
                value={selectedProjectId}
                onChange={handleSelect}
                input={<OutlinedInput label="Project" />}
              >
                {projects.map((project) => (
                  <MenuItem key={project.id} value={project.id}>
                    {project.projectname}
                  </MenuItem>
                ))}
              </Select>
              <Button
                id="project-start-btn"
                style={{ marginTop: 20 }}
                variant="outlined"
                onClick={handleStart}
              >
                start
              </Button>
            </FormControl>
          )}
        </DialogContent>
        {!isAssociate && (
          <DialogActions>
            <Button id="dialog-accept" onClick={handleAssociateAccept}>
              Yes
            </Button>
            <Button id="dialog-cancel" onClick={handleAssociateDismiss}>
              No
            </Button>
          </DialogActions>
        )}
      </Dialog>
      <Dialog open={continueDialog && selectedProjectId !== ""}>
        <DialogTitle>Continue Pomodoro?</DialogTitle>
        <DialogContent id="continue-dlg">
          <DialogContentText>
            Do you want to continue another pomodoro?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button id="continue-accept" onClick={handleContinueDialogAccept}>Yes</Button>
          <Button id="continue-cancel" onClick={handleContinueDialogDismiss}>No</Button>
        </DialogActions>
      </Dialog>
      
      <Dialog open={stopDialog && selectedProjectId !== ""}>
        <DialogTitle>Stop Pomodoro</DialogTitle>
        <DialogContent id="partial-pomo-dlg">
          <DialogContentText>
            Do you want to keep partial pomodoro data?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button id="stop-accept-btn" onClick={handleStopDialogAccept}>Yes</Button>
          <Button id="stop-cancel-btn" onClick={handleStopDialogDismiss}>No</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
