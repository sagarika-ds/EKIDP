package com.ekidp.graph.controller;

import com.ekidp.graph.entity.EmployeeNode;
import com.ekidp.graph.entity.ProjectNode;
import com.ekidp.graph.entity.TechnologyNode;
import com.ekidp.graph.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "*")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @PostMapping("/employee")
    public ResponseEntity<EmployeeNode> createEmployee(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String department,
            @RequestParam String role) {
        return ResponseEntity.ok(
                graphService.createEmployee(name, email, department, role));
    }

    @PostMapping("/project")
    public ResponseEntity<ProjectNode> createProject(
            @RequestParam String name,
            @RequestParam String department,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        return ResponseEntity.ok(
                graphService.createProject(name, department, status));
    }

    @PostMapping("/technology")
    public ResponseEntity<TechnologyNode> createTechnology(
            @RequestParam String name,
            @RequestParam(defaultValue = "General") String category) {
        return ResponseEntity.ok(
                graphService.createTechnology(name, category));
    }

    @PostMapping("/link/employee-project")
    public ResponseEntity<Map<String, Object>> linkEmployeeProject(
            @RequestParam String email,
            @RequestParam String projectName) {
        return ResponseEntity.ok(
                graphService.linkEmployeeToProject(email, projectName));
    }

    @PostMapping("/link/employee-technology")
    public ResponseEntity<Map<String, Object>> linkEmployeeTechnology(
            @RequestParam String email,
            @RequestParam String techName) {
        return ResponseEntity.ok(
                graphService.linkEmployeeToTechnology(email, techName));
    }

    @PostMapping("/link/project-technology")
    public ResponseEntity<Map<String, Object>> linkProjectTechnology(
            @RequestParam String projectName,
            @RequestParam String techName) {
        return ResponseEntity.ok(
                graphService.linkProjectToTechnology(projectName, techName));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeNode>> getAllEmployees() {
        return ResponseEntity.ok(graphService.getAllEmployees());
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectNode>> getAllProjects() {
        return ResponseEntity.ok(graphService.getAllProjects());
    }

    @GetMapping("/technologies")
    public ResponseEntity<List<TechnologyNode>> getAllTechnologies() {
        return ResponseEntity.ok(graphService.getAllTechnologies());
    }

    @GetMapping("/knowledge-graph")
    public ResponseEntity<Map<String, Object>> getKnowledgeGraph() {
        return ResponseEntity.ok(graphService.getKnowledgeGraph());
    }

    @GetMapping("/risk/{email}")
    public ResponseEntity<Map<String, Object>> calculateRisk(
            @PathVariable String email) {
        return ResponseEntity.ok(
                graphService.calculateKnowledgeRisk(email));
    }

    @GetMapping("/high-risk")
    public ResponseEntity<List<EmployeeNode>> getHighRiskEmployees(
            @RequestParam(defaultValue = "70") double threshold) {
        return ResponseEntity.ok(
                graphService.getHighRiskEmployees(threshold));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Graph service is running");
    }
}
