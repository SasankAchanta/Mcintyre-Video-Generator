package org.mcintyrelab.model.enums;

public enum Role {
    ROLE_ADMIN,         // lab manager - full access, manage users, view everything
    ROLE_RESEARCHER,    // runs experiments, uploads videos, views results
    ROLE_TECH           // technical staff - manages the ML models/system
}