package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.ResponseEntity;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.EventService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet("/events/*")
public class EventController extends HttpServlet {
    EventService eventService;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        eventService = (EventService) getServletContext().getAttribute(ContextListener.EVENT_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数

        RequestHandler handler = () -> {
            if (Objects.equals(idParam, "-1")) {
                return listEvents();
            } else {
                try {
                    Integer eventId = Integer.valueOf(idParam); // 将id参数转换为整数
                    return viewEvent(eventId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                            Error.builder()
                                    .status(HttpServletResponse.SC_BAD_REQUEST)
                                    .message("Invalid ID format")
                                    .reason(e.getMessage())
                                    .build()
                    );
                } catch (Exception e) {
                    return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            Error.builder()
                                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                    .message("error")
                                    .reason(e.getMessage())
                                    .build()
                    );
                }
            }
        };

        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL

        if (pathInfo.equals("/save")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> {
                        try {
                            // 解析请求体中的Club数据，假设请求体是JSON格式
                            Event event = parseEventFromRequest(req);

                            // 调用Service层保存Club
                            boolean isSaved = eventService.saveEvent(event);

                            if (isSaved) {
                                return ResponseEntity.ok(null);
                            } else {
                                return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                        Error.builder()
                                                .status(HttpServletResponse.SC_BAD_REQUEST)
                                                .message("Failed to save the event.")
                                                .reason("Failed to save the event.")
                                                .build()
                                );
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_BAD_REQUEST)
                                            .message("Failed to save the event.")
                                            .reason(e.getMessage())
                                            .build()
                            );
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                            .message("An error occurred while saving the event.")
                                            .reason(e.getMessage())
                                            .build()
                            );
                        }
                    }
            ).handle();
            }
    }

    private Event parseEventFromRequest(HttpServletRequest req) throws IOException {
        Event event = mapper.readValue(req.getInputStream(), Event.class);
        System.out.println(event.toString());

        if ( event.getTitle().isEmpty()||event.getTitle() == null ) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }

        return event;
    }

//    private void viewEvent(HttpServletRequest req, HttpServletResponse resp, Integer eventId) throws Exception {
//        PrintWriter out = resp.getWriter();
//        Event event = eventService.getEventById(eventId);
//        if (event != null) {
//            out.write("{\"title\":\"" + event.getTitle() + "\", \"description\":\"" + event.getDescription()+"\", \"club\":\"" + event.getClub() +"\", \"cost\":\"" + event.getCost()+"\", \"venue\":\"" + event.getVenueName()+ "\"}");
//        } else {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            out.write("{\"error\":\"Club not found.\"}");
//        }
//    }
    private ResponseEntity viewEvent(Integer eventId) {
        Event event = null;
        try {
            event = eventService.getEventById(eventId);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.of(HttpServletResponse.SC_NOT_FOUND,
                    Error.builder()
                            .status(HttpServletResponse.SC_NOT_FOUND)
                            .message("Event not found.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

//    private void listEvents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        List<Event> clubs = eventService.getAllEvents();
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//        PrintWriter out = resp.getWriter();
//
//        // Use Gson to convert list to JSON and return
//        Gson gson = new Gson();
//        String json = gson.toJson(clubs);
//        out.print(json);
//        out.flush();
//    }
    private ResponseEntity listEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}
