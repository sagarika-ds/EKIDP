import numpy as np
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
import random

random.seed(42)
np.random.seed(42)

def generate_training_data_success(n=200):
    X = []
    y = []
    for _ in range(n):
        team_size = random.randint(1, 20)
        doc_coverage = random.uniform(0, 100)
        risk_score = random.uniform(0, 100)
        budget_health = random.uniform(0, 100)
        experience_years = random.uniform(0, 15)

        success_prob = (
            (team_size / 20) * 20 +
            (doc_coverage / 100) * 25 +
            ((100 - risk_score) / 100) * 30 +
            (budget_health / 100) * 15 +
            (experience_years / 15) * 10
        )
        success = 1 if success_prob > 50 else 0
        X.append([team_size, doc_coverage, risk_score,
                  budget_health, experience_years])
        y.append(success)
    return np.array(X), np.array(y)

def generate_training_data_attrition(n=200):
    X = []
    y = []
    for _ in range(n):
        tenure_years = random.uniform(0, 10)
        project_count = random.randint(1, 10)
        risk_score = random.uniform(0, 100)
        satisfaction = random.uniform(0, 100)
        workload = random.uniform(0, 100)

        attrition_prob = (
            (10 - tenure_years) / 10 * 20 +
            (project_count / 10) * 15 +
            (risk_score / 100) * 25 +
            ((100 - satisfaction) / 100) * 30 +
            (workload / 100) * 10
        )
        attrition = 1 if attrition_prob > 50 else 0
        X.append([tenure_years, project_count, risk_score,
                  satisfaction, workload])
        y.append(attrition)
    return np.array(X), np.array(y)

def generate_training_data_cost_overrun(n=200):
    X = []
    y = []
    for _ in range(n):
        project_complexity = random.uniform(0, 100)
        team_experience = random.uniform(0, 100)
        scope_changes = random.randint(0, 10)
        timeline_pressure = random.uniform(0, 100)
        vendor_dependency = random.uniform(0, 100)

        overrun_prob = (
            (project_complexity / 100) * 30 +
            ((100 - team_experience) / 100) * 20 +
            (scope_changes / 10) * 25 +
            (timeline_pressure / 100) * 15 +
            (vendor_dependency / 100) * 10
        )
        overrun = 1 if overrun_prob > 50 else 0
        X.append([project_complexity, team_experience, scope_changes,
                  timeline_pressure, vendor_dependency])
        y.append(overrun)
    return np.array(X), np.array(y)

success_X, success_y = generate_training_data_success()
success_model = RandomForestClassifier(n_estimators=100, random_state=42)
success_model.fit(success_X, success_y)

attrition_X, attrition_y = generate_training_data_attrition()
attrition_model = RandomForestClassifier(n_estimators=100, random_state=42)
attrition_model.fit(attrition_X, attrition_y)

cost_X, cost_y = generate_training_data_cost_overrun()
cost_model = RandomForestClassifier(n_estimators=100, random_state=42)
cost_model.fit(cost_X, cost_y)


def predict_project_success(team_size, doc_coverage, risk_score,
                             budget_health, experience_years):
    features = np.array([[team_size, doc_coverage, risk_score,
                          budget_health, experience_years]])
    probability = success_model.predict_proba(features)[0][1]
    prediction = success_model.predict(features)[0]

    factors = []
    if team_size < 3:
        factors.append("Small team size increases risk")
    if doc_coverage < 50:
        factors.append("Low documentation coverage")
    if risk_score > 60:
        factors.append("High knowledge risk score")
    if budget_health < 50:
        factors.append("Budget concerns identified")
    if experience_years < 2:
        factors.append("Limited team experience")

    return {
        "successProbability": round(float(probability) * 100, 2),
        "prediction": "SUCCESS" if prediction == 1 else "AT_RISK",
        "confidenceLevel": "HIGH" if abs(probability - 0.5) > 0.3
                            else "MEDIUM",
        "riskFactors": factors if factors else
                       ["No significant risk factors identified"]
    }


def predict_employee_attrition(tenure_years, project_count,
                               risk_score, satisfaction, workload):
    features = np.array([[tenure_years, project_count, risk_score,
                          satisfaction, workload]])
    probability = attrition_model.predict_proba(features)[0][1]
    prediction = attrition_model.predict(features)[0]

    factors = []
    if tenure_years < 1:
        factors.append("Short tenure increases flight risk")
    if project_count > 5:
        factors.append("High project load may cause burnout")
    if risk_score > 60:
        factors.append("Employee holds critical knowledge - retention priority")
    if satisfaction < 50:
        factors.append("Low satisfaction indicators")
    if workload > 70:
        factors.append("High workload detected")

    return {
        "attritionProbability": round(float(probability) * 100, 2),
        "prediction": "HIGH_RISK" if prediction == 1 else "STABLE",
        "urgency": "IMMEDIATE" if probability > 0.7 else
                   "MONITOR" if probability > 0.4 else "LOW",
        "riskFactors": factors if factors else
                       ["No significant attrition risk factors"]
    }


def predict_cost_overrun(project_complexity, team_experience,
                         scope_changes, timeline_pressure,
                         vendor_dependency):
    features = np.array([[project_complexity, team_experience,
                          scope_changes, timeline_pressure,
                          vendor_dependency]])
    probability = cost_model.predict_proba(features)[0][1]
    prediction = cost_model.predict(features)[0]

    factors = []
    if project_complexity > 70:
        factors.append("High project complexity")
    if team_experience < 40:
        factors.append("Limited team experience with similar projects")
    if scope_changes > 3:
        factors.append("Frequent scope changes detected")
    if timeline_pressure > 70:
        factors.append("Aggressive timeline increases overrun risk")
    if vendor_dependency > 60:
        factors.append("High vendor dependency")

    estimated_overrun_pct = round(float(probability) * 35, 1)

    return {
        "overrunProbability": round(float(probability) * 100, 2),
        "prediction": "LIKELY_OVERRUN" if prediction == 1
                      else "ON_BUDGET",
        "estimatedOverrunPercentage": estimated_overrun_pct,
        "riskFactors": factors if factors else
                       ["No significant cost risk factors"]
    }


def predict_delivery_delay(team_size, doc_coverage, risk_score,
                           scope_changes, timeline_pressure):
    base_delay_score = (
        (max(0, 5 - team_size) / 5) * 25 +
        ((100 - doc_coverage) / 100) * 20 +
        (risk_score / 100) * 25 +
        (scope_changes / 10) * 20 +
        (timeline_pressure / 100) * 10
    )
    delay_probability = min(100, base_delay_score)

    factors = []
    if team_size < 3:
        factors.append("Insufficient team size for timeline")
    if doc_coverage < 50:
        factors.append("Poor documentation slows development")
    if risk_score > 60:
        factors.append("High knowledge risk may cause delays")
    if scope_changes > 3:
        factors.append("Scope creep detected")

    estimated_delay_days = round(delay_probability * 0.6, 0)

    return {
        "delayProbability": round(delay_probability, 2),
        "prediction": "LIKELY_DELAYED" if delay_probability > 50
                      else "ON_TRACK",
        "estimatedDelayDays": int(estimated_delay_days),
        "riskFactors": factors if factors else
                       ["No significant delay factors identified"]
    }
