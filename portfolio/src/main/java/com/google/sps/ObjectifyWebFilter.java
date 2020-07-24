package com.google.sps;

import javax.servlet.annotation.WebFilter;

import com.googlecode.objectify.ObjectifyFilter;

@WebFilter("/*")
public class ObjectifyWebFilter extends ObjectifyFilter {}
