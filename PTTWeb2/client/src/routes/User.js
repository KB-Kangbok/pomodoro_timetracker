import ManageProject from "../components/ManageProject";
import {
  Grid,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Typography,
} from "@mui/material";
import { useState, useEffect } from "react";
import Pomodoro from "../components/Pomodoro";
import axios from "axios";
import { apiUrl } from "../config.json";
import Report from "../components/Report";

export default function User({
  location: {
    state: { id, firstName },
  },
  isTest,
}) {
  const [projects, setProjects] = useState([{ id: 0 }]);
  const [selectedMenu, setSelectedMenu] = useState("project");
  const [update, setUpdate] = useState(false);

  const handleProjectSelect = () => {
    setSelectedMenu("project");
  };
  const handlePomodoroSelect = () => {
    setSelectedMenu("pomodoro");
  };

  const handleReportSelect = () => {
    setSelectedMenu("report");
  };

  useEffect(() => {
    const getProjects = async () => {
      const { data } = await axios.get(`${apiUrl}/users/${id}/projects`);
      setProjects(data);
      setUpdate(false);
    };
    getProjects();
  }, [update, id]);

  return (
    <div
      className="font-sans"
      style={{ margin: 0, marginTop: 0, color: "#414244" }}
    >
      <Typography
        id="greeting"
        component="h6"
        align="right"
        sx={{ marginRight: 5, fontWeight: "bold" }}
      >{`Hi, ${firstName}`}</Typography>
      <Grid container style={{ marginTop: 30 }}>
        <Grid item xs={2} alignItems="center">
          <List sx={{ width: "100%", alignContent: "center" }}>
            <ListItem key={0} disablePadding>
              <ListItemButton id="project-btn" onClick={handleProjectSelect}>
                <ListItemText disableTypography primary={"Manage Project"} />
              </ListItemButton>
            </ListItem>

            <ListItem key={1} disablePadding>
              <ListItemButton id="pomodoro-btn" onClick={handlePomodoroSelect}>
                <ListItemText disableTypography primary={"Pomodoro"} />
              </ListItemButton>
            </ListItem>

            <ListItem key={2} disablePadding>
              <ListItemButton id="report-btn" onClick={handleReportSelect}>
                <ListItemText disableTypography primary={"Report"} />
              </ListItemButton>
            </ListItem>
          </List>
        </Grid>
        <Grid item xs alignContent="center">
          {selectedMenu === "project" && (
            <ManageProject id={id} projects={projects} setUpdate={setUpdate} />
          )}
          {selectedMenu === "pomodoro" && (
            <Pomodoro id={id} projects={projects} isTest={isTest} />
          )}
          {selectedMenu === "report" && <Report id={id} projects={projects} />}
        </Grid>
      </Grid>
    </div>
  );
}
