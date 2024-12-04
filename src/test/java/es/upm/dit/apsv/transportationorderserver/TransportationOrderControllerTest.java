package es.upm.dit.apsv.transportationorderserver;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.dit.apsv.transportationorderserver.controller.TransportationOrderController;
import es.upm.dit.apsv.transportationorderserver.model.TransportationOrder;
import es.upm.dit.apsv.transportationorderserver.repository.TransportationOrderRepository;

@WebMvcTest(TransportationOrderController.class)

public class TransportationOrderControllerTest {

    @InjectMocks

    private TransportationOrderController business;

    @MockBean

    private TransportationOrderRepository repository;

    @Autowired

    private MockMvc mockMvc;

    @Test
    public void testGetOrders() throws Exception {
        //call GET "/transportationorders"  application/json

        when(repository.findAll()).thenReturn(getAllTestOrders());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/transportationorders")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)))
                .andReturn();
    }

    private List<TransportationOrder> getAllTestOrders(){
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<TransportationOrder> orders =
               new ArrayList<TransportationOrder>();
        TransportationOrder order = null;

        try(BufferedReader br = new BufferedReader(new FileReader(
                        new ClassPathResource("orders.json").getFile()))) {
            for(String line; (line = br.readLine()) != null; ) {
              order = objectMapper.readValue(line, TransportationOrder.class);
              orders.add(order);
            }
          } catch (IOException e) {
                e.printStackTrace();
        }
         return orders;
       }
    
    @Test
    public void testGetOrder() throws Exception {
        //call GET "/transportationorders/{truck}"  application/json

        when(repository.findById("8962ZKR")).thenReturn(Optional.of(
                  new TransportationOrder("28","8962ZKR",1591682400000L,
                  40.4562191,-3.8707211,1591692196000L,42.0206372,-4.5330132,
                  0,0.0,0.0,0)));
        // now write the rest of the test case...

        // Construye la solicitud GET con el identificador del camión
            RequestBuilder request = MockMvcRequestBuilders
            .get("/transportationorders/{truck}", "8962ZKR")
            .accept(MediaType.APPLICATION_JSON);

        // Ejecuta la solicitud y verifica los resultados
        MvcResult result = mockMvc.perform(request)
          .andExpect(status().isOk()) // Verifica que la respuesta sea HTTP 200
          .andExpect(jsonPath("$.toid").value("28")) // Verifica el toid (ID del pedido)
          .andExpect(jsonPath("$.truck").value("8962ZKR")) // Verifica el ID del camión
          .andExpect(jsonPath("$.originLat").value(40.4562191)) // Verifica la latitud de origen
          .andExpect(jsonPath("$.originLong").value(-3.8707211)) // Verifica la longitud de origen
          .andExpect(jsonPath("$.dstDate").value(1591692196000L)) // Verifica la fecha de destino
          .andExpect(jsonPath("$.dstLat").value(42.0206372)) // Verifica la latitud de destino
          .andExpect(jsonPath("$.dstLong").value(-4.5330132)) // Verifica la longitud de destino
          .andReturn();

    }
}


