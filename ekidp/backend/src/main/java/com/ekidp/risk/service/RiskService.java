package com.ekidp.risk.service;

import com.ekidp.graph.entity.EmployeeNode;
import com.ekidp.graph.entity.ProjectNode;
import com.ekidp.graph.repository.EmployeeNodeRepository;
import com.ekidp.graph.repository.ProjectNodeRepository;
import com.ekidp.knowledge.repository.DocumentRepository;
import com.ekidp.risk.dto.RiskResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskService {

    private final EmployeeNodeRepository employeeRepo;
    private final ProjectNodeRepository projectRepo;
    private final DocumentRepository documentRepo;

    public RiskService(EmployeeNodeRepository employeeRepo,
                       ProjectNodeRepository projectRepo,
                       DocumentRepository documentRepo) {
        this.employeeRepo = employeeRepo;
        this.projectRepo = projectRepo;
        this.documentRepo = documentRepo;
    }

    public RiskResponse calculateEmployeeRisk(String email) {
        EmployeeNode employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Employee not found: " + email));

        List<String> riskFactors = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        double riskScore = 0.0;

        int techCount = employee.getTechnologies() != null ?
                employee.getTechnologies().size() : 0;
        int projectCount = employee.getProjects() != null ?
                employee.getProjects().size() : 0;
        long totalEmployees = employeeRepo.count();

        if (techCount >= 5) {
            riskScore += 30;
            riskFactors.add("Employee knows " + techCount +
                    " critical technologies");
            recommendations.add("Document all technology knowledge immediately");
        } else if (techCount >= 3) {
            riskScore += 20;
            riskFactors.add("Employee knows " + techCount +
                    " technologies");
            recommendations.add("Create knowledge transfer sessions");
        }

        if (projectCount >= 3) {
            riskScore += 25;
            riskFactors.add("Employee involved in " + projectCount +
                    " projects");
            recommendations.add("Assign backup resources to each project");
        } else if (projectCount >= 2) {
            riskScore += 15;
            riskFactors.add("Employee involved in " + projectCount +
                    " projects");
        }

        long docsUploaded = documentRepo
                .countByDepartment(employee.getDepartment());
        if (docsUploaded < 5) {
            riskScore += 25;
            riskFactors.add("Low documentation in " +
                    employee.getDepartment() + " department");
            recommendations.add("Increase documentation coverage");
        }

        if (totalEmployees < 3) {
            riskScore += 20;
            riskFactors.add("Small team size increases individual risk");
            recommendations.add("Hire and cross-train team members");
        }

        riskScore = Math.min(100.0, riskScore);

        if (riskFactors.isEmpty()) {
            riskFactors.add("No critical risk factors identified");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("Continue current knowledge management practices");
        }

        recommendations.add("Schedule quarterly knowledge review");
        recommendations.add("Maintain updated documentation");

        String riskLevel = riskScore >= 70 ? "CRITICAL" :
                riskScore >= 50 ? "HIGH" :
                riskScore >= 30 ? "MEDIUM" : "LOW";

        Map<String, Object> details = new HashMap<>();
        details.put("technologiesOwned", techCount);
        details.put("projectsInvolved", projectCount);
        details.put("department", employee.getDepartment());
        details.put("role", employee.getRole());
        details.put("totalTeamSize", totalEmployees);
        details.put("departmentDocuments", docsUploaded);

        employee.setRiskScore(riskScore);
        employeeRepo.save(employee);

        return RiskResponse.builder()
                .entityName(employee.getName())
                .entityType("EMPLOYEE")
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .riskFactors(riskFactors)
                .recommendations(recommendations)
                .details(details)
                .message(riskLevel.equals("CRITICAL") ?
                        "URGENT: Immediate action required!" :
                        riskLevel.equals("HIGH") ?
                                "High risk - action needed soon" :
                                "Risk is manageable")
                .build();
    }

    public RiskResponse calculateProjectRisk(String projectName) {
        ProjectNode project = projectRepo.findByName(projectName)
                .orElseThrow(() -> new RuntimeException(
                        "Project not found: " + projectName));

        List<String> riskFactors = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        double riskScore = 0.0;

        List<EmployeeNode> projectEmployees =
                employeeRepo.findByProject(projectName);

        if (projectEmployees.size() == 1) {
            riskScore += 40;
            riskFactors.add("Single person dependency - only 1 employee");
            recommendations.add("Assign at least 2 more resources immediately");
        } else if (projectEmployees.size() == 2) {
            riskScore += 20;
            riskFactors.add("Only 2 employees on project");
            recommendations.add("Consider adding more team members");
        }

        int techCount = project.getTechnologies() != null ?
                project.getTechnologies().size() : 0;
        if (techCount == 0) {
            riskScore += 20;
            riskFactors.add("No technologies documented for project");
            recommendations.add("Document all technologies used");
        }

        long projectDocs = documentRepo.countByDepartment(
                project.getDepartment());
        if (projectDocs < 3) {
            riskScore += 25;
            riskFactors.add("Insufficient project documentation");
            recommendations.add("Create comprehensive project documentation");
        }

        if ("ACTIVE".equals(project.getStatus())) {
            double avgEmployeeRisk = projectEmployees.stream()
                    .mapToDouble(e -> e.getRiskScore() != null ?
                            e.getRiskScore() : 0.0)
                    .average()
                    .orElse(0.0);
            if (avgEmployeeRisk > 60) {
                riskScore += 15;
                riskFactors.add("High average employee risk score");
                recommendations.add("Conduct knowledge transfer sessions");
            }
        }

        riskScore = Math.min(100.0, riskScore);

        String riskLevel = riskScore >= 70 ? "CRITICAL" :
                riskScore >= 50 ? "HIGH" :
                riskScore >= 30 ? "MEDIUM" : "LOW";

        Map<String, Object> details = new HashMap<>();
        details.put("teamSize", projectEmployees.size());
        details.put("technologies", techCount);
        details.put("department", project.getDepartment());
        details.put("status", project.getStatus());
        details.put("documents", projectDocs);
        details.put("teamMembers", projectEmployees.stream()
                .map(EmployeeNode::getName)
                .collect(Collectors.toList()));

        return RiskResponse.builder()
                .entityName(project.getName())
                .entityType("PROJECT")
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .riskFactors(riskFactors)
                .recommendations(recommendations)
                .details(details)
                .message(riskLevel.equals("CRITICAL") ?
                        "URGENT: Project at critical risk!" :
                        "Project risk level: " + riskLevel)
                .build();
    }

    public Map<String, Object> getOrganizationRiskDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        List<EmployeeNode> allEmployees = employeeRepo.findAll();
        List<EmployeeNode> highRiskEmployees =
                employeeRepo.findHighRiskEmployees(70.0);
        List<ProjectNode> allProjects = projectRepo.findAll();

        double avgEmployeeRisk = allEmployees.stream()
                .mapToDouble(e -> e.getRiskScore() != null ?
                        e.getRiskScore() : 0.0)
                .average()
                .orElse(0.0);

        dashboard.put("totalEmployees", allEmployees.size());
        dashboard.put("highRiskEmployees", highRiskEmployees.size());
        dashboard.put("totalProjects", allProjects.size());
        dashboard.put("averageEmployeeRisk",
                Math.round(avgEmployeeRisk * 100.0) / 100.0);
        dashboard.put("organizationRiskLevel",
                avgEmployeeRisk >= 70 ? "CRITICAL" :
                        avgEmployeeRisk >= 50 ? "HIGH" :
                                avgEmployeeRisk >= 30 ? "MEDIUM" : "LOW");

        List<Map<String, Object>> employeeRisks = allEmployees.stream()
                .map(e -> {
                    Map<String, Object> risk = new HashMap<>();
                    risk.put("name", e.getName());
                    risk.put("email", e.getEmail());
                    risk.put("department", e.getDepartment());
                    risk.put("riskScore", e.getRiskScore() != null ?
                            e.getRiskScore() : 0.0);
                    risk.put("riskLevel",
                            e.getRiskScore() != null &&
                                    e.getRiskScore() >= 70 ? "CRITICAL" :
                                    e.getRiskScore() != null &&
                                            e.getRiskScore() >= 50 ? "HIGH" :
                                            e.getRiskScore() != null &&
                                                    e.getRiskScore() >= 30 ?
                                                    "MEDIUM" : "LOW");
                    return risk;
                })
                .collect(Collectors.toList());

        dashboard.put("employeeRisks", employeeRisks);
        dashboard.put("recommendations", generateOrgRecommendations(
                highRiskEmployees.size(), allEmployees.size()));

        return dashboard;
    }

    public Map<String, Object> getKnowledgeGapAnalysis(String department) {
        Map<String, Object> gaps = new HashMap<>();

        List<EmployeeNode> employees =
                employeeRepo.findByDepartment(department);
        long docCount = documentRepo.countByDepartment(department);

        List<String> missingAreas = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        if (docCount < 10) {
            missingAreas.add("Insufficient documentation coverage");
            actions.add("Create at least " + (10 - docCount) +
                    " more documents");
        }
        if (employees.size() < 3) {
            missingAreas.add("Small team - knowledge concentration risk");
            actions.add("Cross-train employees across departments");
        }

        Set<String> allTechs = new HashSet<>();
        for (EmployeeNode emp : employees) {
            if (emp.getTechnologies() != null) {
                emp.getTechnologies()
                        .forEach(t -> allTechs.add(t.getName()));
            }
        }

        if (allTechs.isEmpty()) {
            missingAreas.add("No technology skills documented");
            actions.add("Map all technology skills for each employee");
        }

        gaps.put("department", department);
        gaps.put("employeeCount", employees.size());
        gaps.put("documentCount", docCount);
        gaps.put("documentedTechnologies", allTechs);
        gaps.put("missingAreas", missingAreas);
        gaps.put("recommendedActions", actions);
        gaps.put("gapScore", calculateGapScore(
                employees.size(), (int) docCount, allTechs.size()));

        return gaps;
    }

    private double calculateGapScore(int empCount,
                                     int docCount, int techCount) {
        double score = 100.0;
        if (empCount < 3) score -= 20;
        if (docCount < 5) score -= 30;
        if (techCount == 0) score -= 25;
        return Math.max(0, score);
    }

    private List<String> generateOrgRecommendations(
            int highRiskCount, int totalCount) {
        List<String> recs = new ArrayList<>();
        if (highRiskCount > 0) {
            recs.add("Conduct immediate knowledge transfer for " +
                    highRiskCount + " high-risk employees");
        }
        if (totalCount < 5) {
            recs.add("Consider expanding the team to reduce knowledge concentration");
        }
        recs.add("Implement monthly knowledge documentation reviews");
        recs.add("Create succession plans for critical roles");
        recs.add("Use pair programming and cross-training programs");
        return recs;
    }
}
