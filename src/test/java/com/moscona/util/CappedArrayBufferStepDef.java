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

package com.moscona.util;

import com.moscona.util.collections.CappedArrayBuffer;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created: 2/4/14 9:26 AM
 * By: Arnon Moscona
 */
public class CappedArrayBufferStepDef {
    private CappedArrayBuffer<Integer> cab;

    @Given("^a capped array buffer \\(CAB\\) with size (\\d+)$")
    public void a_capped_array_buffer_CAB_with_size(int size) throws Throwable {
        cab = new CappedArrayBuffer<>(size);
    }

    @When("^I add to the CAB ([\\d+,]*)$")
    public void I_add_to_the_CAB_(String list) throws Throwable {
        String[] broken = list.split(",");
        for (String s: broken) {
            cab.add(Integer.parseInt(s));
        }
    }

    @Then("^the CAB buffer should contain ([\\d+,]*)$")
    public void the_CAB_buffer_should_contain_(String list) throws Throwable {
        assertEquals(cab.toString(), "[" + StringUtils.join(cab, ", ") + "]");
    }

    @And("^I add to the CAB the list ([\\d+,]*)$")
    public void I_add_to_the_CAB_the_list_(String list) throws Throwable {
        String[] broken = list.split(",");
        List<Integer> l = new ArrayList<>();
        for (String s: broken) {
            l.add(Integer.parseInt(s));
        }
        cab.addAll(l);
    }
}
