/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moscona.util.windows;

import com.moscona.exceptions.InvalidStateException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Arnon
 * Date: May 26, 2010
 * Time: 7:50:42 PM
 * A class to do PS and Kill-like operations on Windows
 */
public class TaskList {
    public List<TaskInfo> listTasks(String name) throws InvalidStateException {
        try {
            String path = System.getenv("windir") +"\\system32\\"+"tasklist.exe";
            Process proc = Runtime.getRuntime().exec(path);
            InputStream procOutput = proc.getInputStream ();
//            int exitCode = proc.waitFor();
//            if (0 == exitCode) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(procOutput));
            try {
                ArrayList<TaskInfo> retval = new ArrayList<TaskInfo>();
                String line = "";
                boolean started = false;
                int nameStart = 0;
                int nameEnd = 0;
                int pidStart = 0;
                int pidEnd = 0;

                while (line != null) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    if (started) {
                        String processName = line.substring(nameStart, nameEnd).trim();
                        String pid = line.substring(pidStart, pidEnd).trim();
                        if (name==null || name.equals(processName)) {
                            retval.add(new TaskInfo(processName, Integer.parseInt(pid)));
                        }
                    }
                    else if (line.startsWith("==========")) {
                        started = true;
                        String[] fields = line.split(" +");
                        if (fields.length < 2) {
                            throw new Exception("unexpected structure of response from tasklist.exe");
                        }
                        nameEnd = fields[0].length();
                        pidStart = fields[0].length()+1;
                        pidEnd = pidStart + fields[1].length();
                    }
                }

                return retval;
            } finally {
                reader.close();
            }
//            }
//            else {
//                throw new Exception("tasklist.exe exited with an exit code "+exitCode+" (expected 0)");
//            }

        } catch (Exception e) {
            throw new InvalidStateException("Exception while trying to get task list: "+e,e);
        }
    }

    public List<TaskInfo> listTasks() throws InvalidStateException {
        return listTasks(null);
    }

    public void tryToKillAll(String processName) throws InvalidStateException {
        try {
            String command = "taskkill /IM "+processName+" /F";
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        } catch (Exception e) {
            throw new InvalidStateException("Exception while trying to kill all '"+processName+"'", e);
        }
    }

    public void tryToKill(int pid) throws InvalidStateException {
        try {
            String command = "taskkill /PID "+pid;
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        } catch (Exception e) {
            throw new InvalidStateException("Exception while trying to kill process ID "+pid, e);
        }
    }

    public static class TaskInfo {
        private String name;
        private int pid;

        protected TaskInfo(String name, int pid) {
            this.name = name;
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public int getPid() {
            return pid;
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Starting IQConnect...");
            Runtime.getRuntime().exec("iqconnect.exe");
            System.out.println("Waiting...");
            Thread.sleep(5000);
            System.out.println("Trying...");
            new TaskList().tryToKillAll("iqconnect.exe");
            System.out.println("Done...");
        } catch (Exception e) {
            System.err.println("Oops! "+e);
            e.printStackTrace(System.err);
        }
    }
}

