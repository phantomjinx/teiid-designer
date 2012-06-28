package org.teiid.designer.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelObjectAnnotations;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

public class ModelResourceMockFactory {
	public static ModelResource createModelResource(final String name,
			final String parentPath) throws ModelWorkspaceException {
		final ModelResource parent = mock(ModelResource.class);
		when(parent.getPath()).thenReturn(new Path(parentPath));

		final ModelResource modelResource = mock(ModelResource.class);
		when(modelResource.getItemName()).thenReturn(name);
		when(modelResource.getParent()).thenReturn(parent);
		
		final EmfResource emfResource = mock(EmfResource.class);
        when(modelResource.getEmfResource()).thenReturn(emfResource);
        
        EList<EObject> emptyEList = new BasicEList();
        when(emfResource.getContents()).thenReturn(emptyEList);
        
		return modelResource;
	}

	public static ModelResource createModelResourceWithResourceAnnotation(
			final String name, final String parentPath,
			final boolean createDescription, final String desc,
			final boolean createTags, final Properties props,
			final boolean createKeywords, final Collection<String> kwords)
			throws ModelWorkspaceException {
		final ModelResource modelResource = createModelResource(name,
				parentPath);
		
		ModelObjectAnnotations annotations = createAnnotations();
		ModelAnnotation modelAnnotation = createModelAnnotation();
		when(modelResource.getAnnotations()).thenReturn(annotations);
		when(modelResource.getModelAnnotation()).thenReturn(modelAnnotation);
		Annotation annotation = createAnnotation(createDescription, desc,
				createTags, props, createKeywords, kwords);
		when(annotations.getAnnotation(modelAnnotation)).thenReturn(annotation);

		return modelResource;
	}

	public static ModelResource createModelResourceWithOutResourceAnnotation(
			final String name, final String parentPath)
			throws ModelWorkspaceException {
		final ModelResource modelResource = createModelResource(name,
				parentPath);
		ModelObjectAnnotations annotations = createAnnotations();
		ModelAnnotation modelAnnotation = createModelAnnotation();
		when(modelResource.getAnnotations()).thenReturn(annotations);
		when(modelResource.getModelAnnotation()).thenReturn(modelAnnotation);

		EmfResource emfResource = (EmfResource) modelResource.getEmfResource();
		when(emfResource.getModelAnnotation()).thenReturn(modelAnnotation);
        
		return modelResource;
	}

	public static ModelObjectAnnotations createAnnotations() {
		return mock(ModelObjectAnnotations.class);
	}

	public static ModelAnnotation createModelAnnotation() {
		ModelAnnotation modelAnnotation = mock(ModelAnnotation.class);
		when(modelAnnotation.getModelType()).thenReturn(ModelType.VIRTUAL_LITERAL);
        return modelAnnotation;
	}

	public static Annotation createAnnotation(final boolean createDescription,
			final String desc, final boolean createTags,
			final Properties props, final boolean createKeywords,
			final Collection<String> kwords) {
		Annotation annotation = mock(Annotation.class);
		if (createDescription) {
			when(annotation.getDescription()).thenReturn(desc);
		}
		if (createTags) {
			EMap tags = createTags(props);
			when(annotation.getTags()).thenReturn(tags);
		}
		if (createTags) {
			EList keywords = createKeywords(kwords);
			when(annotation.getKeywords()).thenReturn(keywords);
		}
		return annotation;
	}

	public static EMap createTags(Properties properties) {
		EMap tags = mock(EMap.class);
		if (properties != null && !properties.isEmpty()) {
			Set<Object> keys = properties.keySet();
			for (Object nextKey : keys) {
				when(tags.get(nextKey)).thenReturn(properties.get(nextKey));
			}
		}
		return tags;
	}

	public static EList createKeywords(Collection<String> kWords) {
		EList<String> keywords = mock(EList.class);
		// TODO: FLush this out?
		return keywords;
	}

	public static ModelerCore getModelerCore() {
		return mock(ModelerCore.class);
	}

	public static ModelEditor getModelerEditor() {
		return mock(ModelEditor.class);
	}

	public static ModelContents getModelContents(boolean addAnnotationContainer) {
		ModelContents modelContents = mock(ModelContents.class);
		if (addAnnotationContainer) {
			AnnotationContainer container = getAnnotationContainer();
			when(modelContents.getAnnotationContainer(false)).thenReturn(
					container);
		}
		return mock(ModelContents.class);
	}

	public static AnnotationContainer getAnnotationContainer() {
		return mock(AnnotationContainer.class);
	}
	
    /**
     * @param string
     * @param string2
     * @param string3
     * @return
     */
    public static File createTempFile(String name, String suffix, File tempDir, String contents) throws IOException {
        File tempFile = File.createTempFile(name, suffix, tempDir);
        tempFile.deleteOnExit();
        FileWriter writer = new FileWriter(tempFile);
        writer.write(contents);
        writer.close();
        
        return tempFile;
    }
}
