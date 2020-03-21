package com.medas.rewamp.reportservice.utils;

import javax.persistence.NoResultException;

import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jegatheesh.mageswaran<br>
 *         <b>Created</b> On Mar 21, 2020
 *
 */
@Slf4j
public class QueryUtil {
	private QueryUtil() {
	}

	/**
	 * Method to skip NoResultException with default value
	 * 
	 * @param <T>
	 * @param query
	 * @param clazz
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T skipNRE(Query<?> query, Class<T> clazz, T defaultValue) {
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			log.info("NoResultException occured");
			return defaultValue;
		}
	}

	/**
	 * Method to skip NoResultException with default value and Result Transform
	 * 
	 * @param <T>
	 * @param query
	 * @param clazz
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> T skipNREWithRT(NativeQuery<?> query, Class<T> clazz, T defaultValue) {
		try {
			return (T) query.setResultTransformer(Transformers.aliasToBean(clazz)).getSingleResult();
		} catch (NoResultException e) {
			log.info("NoResultException occured");
			return defaultValue;
		}
	}
}
