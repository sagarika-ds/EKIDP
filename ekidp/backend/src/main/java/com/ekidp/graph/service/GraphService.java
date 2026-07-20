package com.ekidp.graph.service;

import com.ekidp.graph.entity.EmployeeNode;
import com.ekidp.graph.entity.ProjectNode;
import com.ekidp.graph.entity.TechnologyNode;
import com.ekidp.graph.repository.EmployeeNodeRepository;
import com.ekidp.graph.repository.ProjectNodeRepository;
import com.ekidp.graph.repository.TechnologyNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GraphService {

    private final EmployeeNodeRepository employeeRepo;
    private final ProjectNodeRepository projectRepo;
    private final TechnologyNodeRepository techRepo;

    public GraphService(EmployeeNodeRepository employeeRepo,
                        ProjectNodeRepository projectRepo,
                        TechnologyNodeRepository techRepo) {
        this.employeeRepo = employeeRepo;
        this.projectRepo = projectRepo;
        this.techRepo = techRepo;
    }

    @Transactional
    public EmployeeNode createEmployee(String name, String email,
                                       String department, String role) {
        Optional<EmployeeNode> existing = employeeRepo.findByEmail(email);
        if (existing.isPresent()) return existing.get();

        EmployeeNode employee = new EmployeeNode(name, email,
                department, role);
        return employeeRepo.save(employee);
    }

    @Transactional
    public ProjectNode createProject(String name, String department,
                                     String status) {
        Optional<ProjectNode> existing = projectRepo.findByName(name);
        if (existing.isPresent()) return existing.get();

        ProjectNode project = new ProjectNode(name, department, status);
        return projectRepo.save(project);
    }

    @Transactional
    public TechnologyNode createTechnology(String name, String category) {
        Optional<TechnologyNode> existing = techRepo.findByName(name);
        if (existing.isPresent()) return existing.get();

        TechnologyNode tech = new TechnologyNode(name, category);
        return techRepo.save(tech);
    }

    @Transactional
    public Map<String, Object> linkEmployeeToProject(String email,
                                                      String projectName) {
        EmployeeNode employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Employee not found: " + email));
        ProjectNode project = projectRepo.findByName(projectName)
                .orElseThrow(() -> new RuntimeException(
                        "Project not found: " + projectName));

        employee.getProjects().add(project);
        employeeRepo.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Employee linked to project");
        result.put("employee", employee.getName());
        result.put("project", project.getName());
        return result;
    }

    @Transactional
    public Map<String, Object> linkEmployeeToTechnology(String email,
                                                         String techName) {
        EmployeeNode employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Employee not found: " + email));
        TechnologyNode tech = techRepo.findByName(techName)
                .orElseThrow(() -> new RuntimeException(
                        "Technology not found: " + techName));

        employee.getTechnologies().add(tech);
        employeeRepo.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Employee linked to technology");
        result.put("employee", employee.getName());
        result.put("technology", tech.getName());
        return result;
    }

    @Transactional
    public Map<String, Object> linkProjectToTechnology(String projectName,
                                                        String techName) {
        ProjectNode project = projectRepo.findByName(projectName)
                .orElseThrow(() -> new RuntimeException(
                        "Project not found: " + projectName));
        TechnologyNode tech = techRepo.findByName(techName)
                .orElseThrow(() -> new RuntimeException(
                        "Technology not found: " + techName));

        project.getTechnologies().add(tech);
        projectRepo.save(project);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Project linked to technology");
        result.put("project", project.getName());
        result.put("technology", tech.getName());
        return result;
    }

    public List<EmployeeNode> getAllEmployees() {
        return employeeRepo.findAll();
    }

    public List<ProjectNode> getAllProjects() {
        return projectRepo.findAll();
    }

    public List<TechnologyNode> getAllTechnologies() {
        return techRepo.findAll();
    }

    public List<EmployeeNode> getHighRiskEmployees(double threshold) {
        return employeeRepo.findHighRiskEmployees(threshold);
    }

    public Map<String, Object> getKnowledgeGraph() {
        Map<String, Object> graph = new HashMap<>();
        graph.put("employees", employeeRepo.findAll());
        graph.put("projects", projectRepo.findAll());
        graph.put("technologies", techRepo.findAll());
        graph.put("totalEmployees", employeeRepo.count());
        graph.put("totalProjects", projectRepo.count());
        graph.put("totalTechnologies", techRepo.count());
        return graph;
    }

    @Transactional
    public Map<String, Object> calculateKnowledgeRisk(String email) {
        EmployeeNode employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Employee not found: " + email));

        long totalEmployees = employeeRepo.count();
        int techCount = employee.getTechnologies().size();
        int projectCount = employee.getProjects().size();

        double riskScore = 0.0;
        if (totalEmployees > 0) {
            riskScore = Math.min(100.0,
                    (techCount * 15.0 + projectCount * 10.0));
        }

        employee.setRiskScore(riskScore);
        employeeRepo.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("employee", employee.getName());
        result.put("email", employee.getEmail());
        result.put("riskScore", riskScore);
        result.put("riskLevel", riskScore > 70 ? "HIGH" :
                riskScore > 40 ? "MEDIUM" : "LOW");
        result.put("technologiesOwned", techCount);
        result.put("projectsInvolved", projectCount);
        result.put("message", riskScore > 70 ?
                "Critical: This employee owns significant knowledge!" :
                "Risk is manageable");
        return result;
    }
}
