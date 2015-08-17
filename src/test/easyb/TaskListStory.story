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

import com.moscona.util.windows.TaskList

description "task list functionality in Windows"

if (System.properties['os.name'].toLowerCase().contains("windows")) {
    before_each "scenario", {
        given "a TaskList instance", {
            taskList = new TaskList()
        }
        and "a result placeholder", {
            result = null
        }
    }

    scenario "listing processes", {
        when "I call listTasks()", {
            result = taskList.listTasks()
        }
        then "I should get a list of some length", {
            result.size().shouldBeGreaterThan(10)
        }
    }

    scenario "getting details of a specific process name", {
        when "I call listTasks(\"winlogon.exe\")", {
            result = taskList.listTasks("winlogon.exe")
        }
        then "I should get a list of some length", {
            result.size().shouldBeGreaterThan(0)
        }
        and "its should match the name", {
            result[0].name.shouldBe("winlogon.exe")
        }
        and "I should be able to get a PID", {
            result[0].pid.shouldBeGreaterThan(0)
        }
    }
}