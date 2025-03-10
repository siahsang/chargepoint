package com.chargepoint.helper

import jakarta.annotation.Resource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.MultiValueMap

@AutoConfigureMockMvc
open class SpringMockMVCHelper {
    @Resource
    protected var mockMvc: MockMvc? = null

    fun performDeleteRequest(path: String, content: String, expectedStatus: Int): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders.delete(path)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
        return responseContentAsString(mvcResult)
    }

    fun performPostRequest(path: String, content: String, expectedStatus: Int): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders.post(path)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }

    fun performPostRequest(
        path: String,
        parameters: MultiValueMap<String?, String?>,
        content: String,
        expectedStatus: Int
    ): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders.post(path)
                .params(parameters)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }


    fun performPutRequest(
        path: String, parameters: MultiValueMap<String?, String?>,
        content: String, expectedStatus: Int
    ): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders.put(path)
                .params(parameters)
                .content(content)
                .contentType(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }

    fun performPutRequest(path: String, content: String, expectedStatus: Int): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders.put(path)
                .content(content)
                .contentType(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }

    fun performGetRequest(path: String, expectedStatus: Int): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders
                .get(path)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }

    fun performGetRequest(path: String, parameters: MultiValueMap<String?, String?>, expectedStatus: Int): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders
                .get(path)
                .params(parameters)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }


    fun performGetRequest(path: String, expectedStatus: Int, httpHeaders: HttpHeaders): String {
        val mvcResult = mockMvc!!.perform(
            MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        return responseContentAsString(mvcResult)
    }


    private fun responseContentAsString(mvcResult: MvcResult): String {
        return mvcResult.response.contentAsString
    }
}
